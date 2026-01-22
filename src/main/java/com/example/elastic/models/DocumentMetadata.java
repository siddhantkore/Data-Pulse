package com.example.elastic.models;

import com.example.elastic.models.enums.DocumentStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "documents")
public class DocumentMetadata {

    @Id
    private String id;
    private String fileName;
    private String s3Key;
    private String extractedText; // we will be not using this in upcoming days, modify to remove it

    // Core LLM response fields
    private List<String> categories;
    private List<String> keywords;

    private Metadata metadata;  // nested object for standard fields

    // Flexible map for document-specific fields - May Vary
    private Map<String, Object> documentSpecificFields;

    @Data
    public static class Metadata {
        private String title;
        private String authorOrSender;
        private String date;
        private List<String> entities;
        private String summary;

    }

//    private List<String> labels;  to take manual labels from user

    private DocumentStatus documentStatus;

//    private String hashsha256;

//    private Map<String, String> history; // Map<user, action>

    private LocalDateTime createdAtLocalDateTime = LocalDateTime.now();

    private LocalDateTime updatedAtLocalDateTime;

}

/**
 * fields to be included
 * hash sha256 for duplicate identification
 * access and update history for tracking
 *
 */