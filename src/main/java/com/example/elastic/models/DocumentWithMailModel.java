package com.example.elastic.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
public class DocumentWithMailModel {
    @Id
    private String id;

    @NonNull
    private String sender;
    private String subject;
    private LocalDateTime sentOn;
    private boolean haveAttachment;
    /*{
        private String email_id: <unique_message_id_or_uid>
        private String subject: Invoice for March 2025
        private Map<String, String> from
              name -> ABC Corp
              email -> accounts@abccorp.com
        private to: [{"name": "Tom Hagen", "email": "tom@nivalcloud.com"}],
        LocalDateTime received_at;

        DocumentMetadata document metadata;
        boolean has_attachments
        int attachment_count;
        enum priority;
        boolean is_read;
        List<String> labels": ["Finance", "Invoices"]

    }*/
}
