package com.example.elastic.connectors.mail_connectors;

import com.example.elastic.sandbox.Sandbox;
import com.example.elastic.services.DocumentProcessorService;
import com.example.elastic.utils.InMemoryMultipartFile;
import jakarta.mail.BodyPart;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.ReceivedDateTerm;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GmailIMAPConnector implements EmailConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(GmailIMAPConnector.class);

    private final String username;

    private final String password;

    @Autowired
    private DocumentProcessorService documentProcessorService;

//    @Autowired
//    private MailModelDTO mailModelDTO;

    public GmailIMAPConnector(@Value("${spring.mail.username}") String user,
                              @Value("${spring.mail.password}") String password) {
        this.username = user;
        this.password = password;
    }

    public Session createSession() {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        return Session.getInstance(props, null);
    }

    public Store createStore(Session session) throws MessagingException {
        return session.getStore("imaps");
    }

    public Folder getInbox(Store store) throws MessagingException {
        return store.getFolder("INBOX");
    }

    /**
     * Next: Configure itself as Using MultipartFile
     * Next: Fetching Mail Data using a DTO as we are already pulling mails
     * Enhancement: Configure try and catch - currently row use
     * Will return fetched attachments from email as List of File
     *
     * @return list files of type File - currently using a helper to convert it to MultipartFile
     */

    @Override
    public List<MultipartFile> fetchAttachments() {
        List<MultipartFile> attachments = new ArrayList<>();
        Store store = null;
        Folder inbox = null;

        try {
            Session session = createSession();
            store = createStore(session);
            store.connect("imap.gmail.com", username, password);
            LOGGER.info("Connected to Gmail IMAP.");

            // Open the INBOX folder
            inbox = getInbox(store);
            inbox.open(Folder.READ_ONLY);

            // Get messages from the inbox
            // specify the criteria - By Date, By unread with SEEN etc.
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -2); // last 1 day
            Date sinceDate = cal.getTime();

            Message[] messages = inbox.search(new ReceivedDateTerm(ComparisonTerm.GT, sinceDate));
            Sandbox sandbox = new Sandbox();
            sandbox.printMessages(messages);
            LOGGER.info("Found {} messages.", messages.length);

            for (Message message : messages) {

                // Emails can contain plain text or multiple parts (attachments, body, etc.)
                Object content = message.getContent();

                // If email content is multipart, process each part separately
                if (content instanceof Multipart multipart) {
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);

                        // Check if this part is an attachment (based on disposition or filename)
                        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
                                || bodyPart.getFileName() != null) {

                            MimeBodyPart mimeBodyPart = (MimeBodyPart) bodyPart;
                            String fileName = mimeBodyPart.getFileName();

                            // Step 5: Read attachment into memory (as bytes)
                            try (InputStream is = mimeBodyPart.getInputStream();
                                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                                // Copy attachment data into byte array
                                is.transferTo(baos);

                                // Step 6: Wrap bytes into a MultipartFile (in-memory)
                                MultipartFile multipartFile = new InMemoryMultipartFile(
                                        fileName,                     // form field name
                                        fileName,                     // original filename
                                        bodyPart.getContentType(),    // MIME type
                                        baos.toByteArray()            // actual bytes
                                );

                                // Add to list for return
                                attachments.add(multipartFile);
                                LOGGER.info("Fetched attachment: {}", fileName);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error fetching attachments: {}", e.getMessage(), e);
        } finally {
            try {
                if (inbox != null && inbox.isOpen()) inbox.close(false);
                if (store != null) store.close();
            } catch (MessagingException e) {
                LOGGER.warn("Error closing mail resources: {}", e.getMessage());
            }
        }

        LOGGER.info("Returning {} attachments as MultipartFile.", attachments.size());
        return attachments;
    }

    /**
     * Converts File into MultipartFile by using Anonymous class
     * Calls to @fetchAttachments Method
     * Enhancement: Remove It
     * @return It's not permanent implementation as of now return bool for easy API access in controllers
     * Sends attachments directly to DocumentProcessorService.
     */
    public boolean sendToDocumentService() {
        try {
            List<MultipartFile> files = fetchAttachments();
            for (MultipartFile multipartFile : files) {
                byte[] bytes = multipartFile.getBytes();
                String fileName = multipartFile.getOriginalFilename();
                String docId = java.util.UUID.randomUUID().toString();
                documentProcessorService.processAndStore(bytes, docId, fileName);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Error sending attachments to document services: {}", e.getMessage(), e);
            return false;
        }
    }

}
