package com.example.elastic.services.processors;

import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.models.enums.DocumentStatus;
import com.example.elastic.repository.DocumentElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for persisting documents to Elasticsearch.
 * Handles all database operations related to document storage and retrieval.
 */
@Service
public class DocumentPersistenceService {

    private final DocumentElasticsearchRepository documentRepository;

    public DocumentPersistenceService(DocumentElasticsearchRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * Creates a new pending document in Elasticsearch.
     *
     * @param documentMetadata the document metadata to save
     * @return saved document with generated ID and timestamps
     */
    public DocumentMetadata createPendingDocument(DocumentMetadata documentMetadata) {
        String id = UUID.randomUUID().toString();
        documentMetadata.setId(id);
        documentMetadata.setDocumentStatus(DocumentStatus.PENDING);
        documentMetadata.setCreatedAtLocalDateTime(LocalDateTime.now());
        documentMetadata.setUpdatedAtLocalDateTime(LocalDateTime.now());
        return documentRepository.save(documentMetadata);
    }

    /**
     * Retrieves a document by its ID.
     *
     * @param documentId the document ID
     * @return Optional containing the document if found
     */
    public Optional<DocumentMetadata> getDocumentById(String documentId) {
        return documentRepository.findById(documentId);
    }

    /**
     * Updates the status of a document.
     *
     * @param documentId the document ID
     * @param status the new status
     */
    public void updateDocumentStatus(String documentId, DocumentStatus status) {
        documentRepository.findById(documentId).ifPresent(doc -> {
            doc.setDocumentStatus(status);
            doc.setUpdatedAtLocalDateTime(LocalDateTime.now());
            documentRepository.save(doc);
        });
    }

    /**
     * Saves a processed document to Elasticsearch.
     *
     * @param documentMetadata the document to save
     * @return saved document
     */
    public DocumentMetadata saveDocument(DocumentMetadata documentMetadata) {
        documentMetadata.setUpdatedAtLocalDateTime(LocalDateTime.now());
        return documentRepository.save(documentMetadata);
    }

    /**
     * Marks a document as failed with error status.
     *
     * @param documentId the document ID
     * @param errorMessage error description (optional)
     */
    public void markAsFailedWithError(String documentId, String errorMessage) {
        documentRepository.findById(documentId).ifPresent(doc -> {
            doc.setDocumentStatus(DocumentStatus.PARSING_FAILED);
            doc.setUpdatedAtLocalDateTime(LocalDateTime.now());
            documentRepository.save(doc);
        });
    }

    /**
     * Deletes a document by its ID.
     *
     * @param documentId the document ID to delete
     * @return true if document was deleted, false if not found
     */
    public boolean deleteDocument(String documentId) {
        if (documentRepository.existsById(documentId)) {
            documentRepository.deleteById(documentId);
            return true;
        }
        return false;
    }

    /**
     * Checks if a document exists.
     *
     * @param documentId the document ID
     * @return true if document exists, false otherwise
     */
    public boolean exists(String documentId) {
        return documentRepository.existsById(documentId);
    }
}
