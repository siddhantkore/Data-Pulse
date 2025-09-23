package com.example.elastic.connectors.mail_connectors;

import java.io.File;
import java.util.List;

public interface EmailConnector {
    /**
     * Fetches new attachments from an email account.
     *
     * @return Currently :- List of Files
     * @throws Exception if fetching fails
     */
    List<File> fetchAttachments() throws Exception;
}
