package com.example.elastic.connector.mail_connectors;

import com.example.elastic.connectors.mail_connectors.GmailIMAPConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GmailIMAPConnectorTest {

    // Using @Mock to create mock objects for the Jakarta Mail API
    @Mock
    private Session mockSession;
    @Mock
    private Store mockStore;
    @Mock
    private Folder mockFolder;
    @Mock
    private Message mockMessage;
    @Mock
    private Multipart mockMultipart;
    @Mock
    private MimeBodyPart mockAttachmentPart;

    // The class under test. Mockito will automatically inject the mocks above
    // into the connector's constructor.
    @InjectMocks
    private GmailIMAPConnector connector;

    private static final String MOCK_USERNAME = "testuser@gmail.com";
    private static final String MOCK_PASSWORD = "testpassword";

    @BeforeEach
    public void setUp() {
        // Mockito will automatically instantiate 'connector' and inject the @Mock fields.
        // No manual instantiation is needed here.
    }

    @Test
    public void testFetchAttachments_Success() throws Exception {
        // Use a try-with-resources block for static mocks to ensure proper cleanup.
        try (MockedStatic<Session> sessionMockedStatic = Mockito.mockStatic(Session.class)) {
            // Mock the static Session.getInstance method
            sessionMockedStatic.when(() -> Session.getInstance(Mockito.any(), Mockito.any())).thenReturn(mockSession);

            // Define the behavior of our mock objects to simulate a successful connection
            Mockito.when(mockSession.getStore("imaps")).thenReturn(mockStore);
            Mockito.when(mockStore.getFolder("INBOX")).thenReturn(mockFolder);

            // Mock the message content to contain a single multipart message with an attachment
            Mockito.when(mockFolder.getMessages()).thenReturn(new Message[]{mockMessage});
            Mockito.when(mockMessage.getContent()).thenReturn(mockMultipart);
            Mockito.when(mockMultipart.getCount()).thenReturn(1);
            Mockito.when(mockMultipart.getBodyPart(0)).thenReturn(mockAttachmentPart);

            // Mock the attachment's properties
            Mockito.when(mockAttachmentPart.getDisposition()).thenReturn(Part.ATTACHMENT);
            Mockito.when(mockAttachmentPart.getFileName()).thenReturn("test_attachment.txt");

            // Prepare mock file content
            String fileContent = "This is a mock attachment.";
            InputStream mockInputStream = new ByteArrayInputStream(fileContent.getBytes());
            Mockito.when(mockAttachmentPart.getInputStream()).thenReturn(mockInputStream);

            // Test a successful scenario where an email has one attachment.
            List<File> attachments = connector.fetchAttachments();

            // Verify that the attachment list is not empty and contains one file.
            assertNotNull(attachments);
            assertEquals(1, attachments.size());

            // Verify that the file object has the correct name.
            File attachmentFile = attachments.get(0);
            assertEquals("test_attachment.txt", attachmentFile.getName());
        }
    }

    @Test
    public void testFetchAttachments_NoAttachments() throws Exception {
        // Use a try-with-resources block for static mocks to ensure proper cleanup.
        try (MockedStatic<Session> sessionMockedStatic = Mockito.mockStatic(Session.class)) {
            sessionMockedStatic.when(() -> Session.getInstance(Mockito.any(), Mockito.any())).thenReturn(mockSession);
            Mockito.when(mockSession.getStore("imaps")).thenReturn(mockStore);
            Mockito.when(mockStore.getFolder("INBOX")).thenReturn(mockFolder);

            // Mock the message content to contain no attachments
            Mockito.when(mockFolder.getMessages()).thenReturn(new Message[]{mockMessage});
            Mockito.when(mockMessage.getContent()).thenReturn(mockMultipart);
            Mockito.when(mockMultipart.getCount()).thenReturn(0);

            List<File> attachments = connector.fetchAttachments();

            // Verify that the returned list is empty.
            assertNotNull(attachments);
            assertTrue(attachments.isEmpty());
        }
    }

    @Test
    public void testFetchAttachments_ConnectionFailure() throws Exception {
        try (MockedStatic<Session> sessionMockedStatic = Mockito.mockStatic(Session.class)) {
            sessionMockedStatic.when(() -> Session.getInstance(Mockito.any(), Mockito.any()))
                    .thenReturn(mockSession);
            Mockito.when(mockSession.getStore("imaps")).thenReturn(mockStore);

            // Match the exact signature and allow nulls
            Mockito.doThrow(new AuthenticationFailedException("Connection failed"))
                    .when(mockStore)
                    .connect(Mockito.eq("imap.gmail.com"),
                            Mockito.nullable(String.class),
                            Mockito.nullable(String.class));

            assertThrows(AuthenticationFailedException.class, () -> connector.fetchAttachments());
        }
    }

}
