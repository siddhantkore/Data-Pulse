package com.example.elastic.models;

import com.example.elastic.models.enums.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

/**
 * Document metadata model stored and indexed in Elasticsearch.
 * Contains document information, extracted content, and processing metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "documents")
public class DocumentMetadata {

    /**
     * Unique document identifier.
     */
    @Id
    private String id;

    /**
     * Original filename of the uploaded document.
     */
    @Field(type = FieldType.Keyword)
    private String fileName;

    /**
     * S3/blob storage key for document file retrieval.
     */
    @Field(type = FieldType.Keyword)
    private String s3Key;

    /**
     * Full text extracted from document via OCR or native extraction.
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String extractedText;

    /**
     * Document categories assigned by LLM.
     */
    @Field(type = FieldType.Keyword)
    private List<String> categories;

    /**
     * Keywords extracted from document by LLM.
     */
    @Field(type = FieldType.Keyword)
    private List<String> keywords;

    /**
     * Structured metadata extracted from document.
     */
    private Metadata metadata;

    /**
     * Flexible map for document-specific fields.
     * Varies based on document type.
     */
    @Field(type = FieldType.Object)
    private Map<String, Object> documentSpecificFields;

    /**
     * Current processing status of the document.
     */
    @Field(type = FieldType.Keyword)
    private DocumentStatus documentStatus;

    /**
     * Timestamp when document was created.
     */
    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX||uuuu-MM-dd'T'HH:mm:ss.SSS||uuuu-MM-dd")
    private Object createdAtLocalDateTime = OffsetDateTime.now(ZoneOffset.UTC);

    /**
     * Timestamp when document was last updated.
     */
    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX||uuuu-MM-dd'T'HH:mm:ss.SSS||uuuu-MM-dd")
    private Object updatedAtLocalDateTime;

    /**
     * MIME type of the document.
     */
    @Field(type = FieldType.Keyword)
    private String mimeType;

    /**
     * File size in bytes.
     */
    @Field(type = FieldType.Long)
    private Long fileSizeBytes;

    /**
     * Document source (upload, gdrive, email, etc).
     */
    @Field(type = FieldType.Keyword)
    private String source;

    /**
     * Document title extracted from metadata or content.
     */
    @Field(type = FieldType.Text)
    private String title;

    /**
     * Relevance score calculated during search ranking (0.0 to 1.0).
     * Not persisted in Elasticsearch, used for result ranking only.
     */
    private transient Double relevanceScore = 0.0;

    /**
     * Nested metadata object for structured field organization.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {

        /**
         * Document title.
         */
        @Field(type = FieldType.Text)
        private String title;

        /**
         * Author or sender of the document.
         */
        @Field(type = FieldType.Keyword)
        private String authorOrSender;

        /**
         * Document date (publication, creation, etc).
         */
        @Field(type = FieldType.Keyword)
        private String date;

        /**
         * Named entities extracted from document.
         */
        @Field(type = FieldType.Keyword)
        private List<String> entities;

        /**
         * Generated summary of document content.
         */
        @Field(type = FieldType.Text)
        private String summary;
    }
}