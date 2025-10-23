package com.example.elastic.connector.mail_connectors;

import com.example.elastic.connectors.mail_connectors.GmailIMAPConnector;
import com.example.elastic.services.DocumentProcessorService;
import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GmailIMAPConnectorTest {

    // Mock Jakarta Mail objects
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
    @Mock
    private DocumentProcessorService mockDocumentProcessorService;

    // Inject mocks into GmailIMAPConnector
    @InjectMocks
    private GmailIMAPConnector connector = new GmailIMAPConnector("testuser@gmail.com", "testpassword");

    @BeforeEach
    public void setUp() throws Exception {
        // Replace default services with mocked one
        connector = spy(new GmailIMAPConnector("testuser@gmail.com", "testpassword"));
        connector = Mockito.mock(GmailIMAPConnector.class, CALLS_REAL_METHODS);
        connector = new GmailIMAPConnector("testuser@gmail.com", "testpassword");
        connector = spy(connector);

        // Inject mocked DocumentProcessorService
        var field = GmailIMAPConnector.class.getDeclaredField("documentProcessorService");
        field.setAccessible(true);
        field.set(connector, mockDocumentProcessorService);
    }

    @Test
    public void testFetchAttachments_Success() throws Exception {
        // Mock Session.getInstance behavior
        doReturn(mockSession).when(connector).createSession();
        doReturn(mockStore).when(connector).createStore(mockSession);
        doReturn(mockFolder).when(connector).getInbox(mockStore);

        // Mock IMAP store connection
        doNothing().when(mockStore).connect(anyString(), anyString(), anyString());
        doNothing().when(mockFolder).open(Folder.READ_ONLY);

        // Prepare fake message with one attachment
        when(mockFolder.search(any())).thenReturn(new Message[]{mockMessage});
        when(mockMessage.getContent()).thenReturn(mockMultipart);
        when(mockMultipart.getCount()).thenReturn(1);
        when(mockMultipart.getBodyPart(0)).thenReturn(mockAttachmentPart);

        when(mockAttachmentPart.getDisposition()).thenReturn(Part.ATTACHMENT);
        when(mockAttachmentPart.getFileName()).thenReturn("test_attachment.txt");
        when(mockAttachmentPart.getContentType()).thenReturn("text/plain");

        // Prepare mock content stream
        String fileContent = "This is a mock attachment.";
        InputStream mockInputStream = new ByteArrayInputStream(fileContent.getBytes());
        when(mockAttachmentPart.getInputStream()).thenReturn(mockInputStream);

        // Call method
        List<MultipartFile> attachments = connector.fetchAttachments();

        // Verify results
        assertNotNull(attachments);
        assertEquals(1, attachments.size());
        MultipartFile multipartFile = attachments.get(0);
        assertEquals("test_attachment.txt", multipartFile.getOriginalFilename());
        assertEquals("text/plain", multipartFile.getContentType());
        assertArrayEquals(fileContent.getBytes(), multipartFile.getBytes());
    }

    @Test
    public void testFetchAttachments_NoAttachments() throws Exception {
        doReturn(mockSession).when(connector).createSession();
        doReturn(mockStore).when(connector).createStore(mockSession);
        doReturn(mockFolder).when(connector).getInbox(mockStore);
        doNothing().when(mockStore).connect(anyString(), anyString(), anyString());
        doNothing().when(mockFolder).open(Folder.READ_ONLY);

        // Message exists but has no attachments
        when(mockFolder.search(any())).thenReturn(new Message[]{mockMessage});
        when(mockMessage.getContent()).thenReturn(mockMultipart);
        when(mockMultipart.getCount()).thenReturn(0);

        List<MultipartFile> attachments = connector.fetchAttachments();

        assertNotNull(attachments);
        assertTrue(attachments.isEmpty());
    }

    @Test
    public void testFetchAttachments_ConnectionFailure() throws Exception {
        // Simulate Session and Store creation
        doReturn(mockSession).when(connector).createSession();
        doReturn(mockStore).when(connector).createStore(mockSession);

        // Throw an exception when connecting
        doThrow(new AuthenticationFailedException("Connection failed"))
                .when(mockStore).connect(anyString(), anyString(), anyString());

        // Expect method to handle the exception and return an empty list
        List<MultipartFile> result = connector.fetchAttachments();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSendToDocumentService_CallsProcessorService() throws Exception {
        // Prepare mock file
        MultipartFile mockFile = mock(MultipartFile.class);
        doReturn(List.of(mockFile)).when(connector).fetchAttachments();

        // Execute
        boolean result = connector.sendToDocumentService();

        // Verify processing
        verify(mockDocumentProcessorService, times(1)).processAndStore(mockFile);
        assertTrue(result);
    }
}
