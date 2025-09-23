/*package com.example.elastic.service;

import com.example.elastic.config.EmailConfig;
import com.example.elastic.connectors.mail_connectors.EmailConnector;
import com.example.elastic.connectors.mail_connectors.GmailIMAPConnector;
import com.example.elastic.connectors.mail_connectors.OutlookIMAPConnector;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;


/**
 * EmailIngestionService provides a unified interface for fetching email attachments
 * regardless of the underlying provider (Gmail, Outlook, etc.).
 */
/*
@Service
public class EmailIngestionService {


    private final EmailConnector connector;

    /**
    * Construct adapter for a given provider.
    *
    * @param provider one of "gmail", "outlook"
    * @param config   credentials / tokens needed for that provider
    *
    public EmailIngestionService(String provider, EmailConfig config) {
        switch (provider.toLowerCase()) {
            case "gmail":
                this.connector = new GmailIMAPConnector(config.getUser(), config.getPassword());
                break;
            case "outlook":
                this.connector = new OutlookIMAPConnector(config.getAccessToken());
                break;
            default:
                throw new IllegalArgumentException("Unsupported email provider: " + provider);
        }

    }
    /**
     * Fetch attachments via the configured connector.
     *
    public List<File> fetchAttachments() throws Exception {
        return connector.fetchAttachments();
    }
}
*/