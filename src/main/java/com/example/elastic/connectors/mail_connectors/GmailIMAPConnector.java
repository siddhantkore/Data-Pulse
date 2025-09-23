package com.example.elastic.connectors.mail_connectors;

import com.example.elastic.model.MailModelDTO;
import com.example.elastic.sandbox.Sandbox;
import com.example.elastic.service.DocumentService;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import jakarta.mail.Folder;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.ReceivedDateTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Date;
import java.util.Calendar;


/**
 * Service to fetch Mails and attachments from Gmail
 *
 * Inserts username and password form application.properties using @Value
 */
@Service
public class GmailIMAPConnector implements EmailConnector {

    private static final Logger logger = LoggerFactory.getLogger(GmailIMAPConnector.class);

    private final String username;

    private final String password;

    @Autowired
    private DocumentService documentService;

//    @Autowired
//    private MailModelDTO mailModelDTO;

    public GmailIMAPConnector(@Value("${spring.mail.username}") String user,
                              @Value("${spring.mail.password}") String password) {
        this.username = user;
        this.password = password;
    }

    protected Session createSession() {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        return Session.getInstance(props, null);
    }

    protected Store createStore(Session session) throws MessagingException {
        return session.getStore("imaps");
    }

    protected Folder getInbox(Store store) throws MessagingException {
        return store.getFolder("INBOX");
    }

    /**
     * Next: Configure itself as Using MultipartFile
     * Next: Fetching Mail Data using a DTO as we are already pulling mails
     * Enhancement: Configure try and catch - currently row use
     * Will return fetched attachments from email as List<File>
     * @return list files of type File - currently using a helper to convert it to MultipartFile
     */
    @Override
    public List<File> fetchAttachments() {
        Store store = null;
        Folder inbox = null;

        List<File> attachments = new ArrayList<>();
        try {

            Session session = createSession();
            store = null;
            inbox = null;

            logger.info("Going to fetch Attachments");
            try {
                // Connect to the IMAP server
                store = createStore(session);
                store.connect("imap.gmail.com", username, password);
                logger.info("Connected to Mail");

                // Open the INBOX folder
                inbox = getInbox(store);
                inbox.open(Folder.READ_ONLY);

                // Get messages from the inbox
                // specify the criteria - By Date, By unread with SEEN etc.
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -2); // last 1 day
                Date yesterday = cal.getTime();

                // implement the criteria
                Message[] messages = inbox.search(new ReceivedDateTerm(ComparisonTerm.GT, yesterday));
                Sandbox sandbox = new Sandbox();
                sandbox.printMessages(messages);
                logger.info("Found {} messages.", messages.length);

                for (Message message : messages) {
                    // Check if the message is a multipart message
                    if (message.getContent() instanceof Multipart) {
                        Multipart multipart = (Multipart) message.getContent();

                        // Iterate through each part of the multipart message
                        for (int i = 0; i < multipart.getCount(); i++) {
                            BodyPart bodyPart = multipart.getBodyPart(i);

                            // Check if the body part is an attachment
                            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) || bodyPart.getFileName() != null) {
                                MimeBodyPart mimeBodyPart = (MimeBodyPart) bodyPart;
                                String fileName = mimeBodyPart.getFileName();

                                // Create a new file for the attachment
                                File attachmentFile = new File(fileName);
                                try (InputStream is = mimeBodyPart.getInputStream();
                                     FileOutputStream fos = new FileOutputStream(attachmentFile)) {
                                    byte[] buffer = new byte[4096];
                                    int bytesRead;
                                    while ((bytesRead = is.read(buffer)) != -1) {
                                        fos.write(buffer, 0, bytesRead);
                                    }
                                    logger.info("Downloaded attachment: {} ", fileName);
                                    attachments.add(attachmentFile);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            logger.warn(e.getLocalizedMessage());
        } finally {
            // Close resources in a finally block
            try {
                if (inbox != null && inbox.isOpen()) {
                    inbox.close(false); // 'false' means don't expunge deleted messages
                }
                if (store != null) {
                    store.close();
                }
            } catch (Exception e) {
                logger.warn(e.getLocalizedMessage());
            }

        }
        logger.info("Attachment Returned");
        return attachments;
    }

    /**
     * Converts File into MultipartFile by using Anonymous class
     * Calls to @fetchAttachments Method
     * Enhancement: Remove It
     * @return It's not permanent implementation as of now return bool for easy API access in controller
     */
    public boolean sendToDocumentService() {
        try {
            List<File> files = fetchAttachments();
            for (File file : files) {
                MultipartFile multipartFile = new MultipartFile()
                {
                    @Override
                    public String getName() {
                    return file.getName();
                }

                    @Override
                    public String getOriginalFilename() {
                    return file.getName();
                }

                    @Override
                    public String getContentType() {
                    return "application/octet-stream";
                }

                    @Override
                    public boolean isEmpty() {
                    return file.length() == 0;
                }

                    @Override
                    public long getSize() {
                    return file.length();
                }

                    @Override
                    public byte[] getBytes() throws IOException {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        return fis.readAllBytes();
                    }
                }

                    @Override
                    public InputStream getInputStream() throws IOException {
                    return new FileInputStream(file);
                }

                    @Override
                    public void transferTo(File dest) throws IOException, IllegalStateException {
                    try (InputStream in = new FileInputStream(file);
                         OutputStream out = new FileOutputStream(dest)) {
                        byte[] buffer = new byte[4096];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    }
                }
                };

                documentService.processAndStore(multipartFile);
            }
            return true;
        } catch (Exception e) {
            logger.warn(e.getLocalizedMessage());
        }
        return false;
    }

}
