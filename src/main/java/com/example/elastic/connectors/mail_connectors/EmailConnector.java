package com.example.elastic.connectors.mail_connectors;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EmailConnector {
    /**
     * Fetches new attachments from an email account.
     *
     * @return Currently :- List of Files
     * @throws Exception if fetching fails
     */
    List<MultipartFile> fetchAttachments() throws Exception;
}
