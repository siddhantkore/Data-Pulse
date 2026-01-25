package com.example.elastic.services.processors;

import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.models.enums.DocumentStatus;
import com.example.elastic.repository.DocumentMongoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Handles all database operations related to document persistence.
 * Single Responsibility: Persist and retrieve documents from database
 */
@Service
public class DocumentPersistenceService {

    private final DocumentMongoRepository documentRepository;

    public DocumentPersistenceService(DocumentMongoRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * Create a new pending document in database
     * @param documentMetadata The document metadata to save
     * @return Saved document with generated ID
     */
    public DocumentMetadata createPendingDocument(DocumentMetadata documentMetadata) {
        String id = UUID.randomUUID().toString();
        documentMetadata.setId(id);
        documentMetadata.setDocumentStatus(DocumentStatus.PENDING);
        return documentRepository.save(documentMetadata);
    }

    /**
     * Retrieve document by ID
     * @param documentId The document ID
     * @return Optional containing the document if found
     */
    public Optional<DocumentMetadata> getDocumentById(String documentId) {
        return documentRepository.findById(documentId);
    }

    /**
     * Update document status
     * @param documentId The document ID
     * @param status The new status
     */
    public void updateDocumentStatus(String documentId, DocumentStatus status) {
        documentRepository.findById(documentId).ifPresent(doc -> {
            doc.setDocumentStatus(status);
            documentRepository.save(doc);
        });
    }

    /**
     * Save processed document
     * @param documentMetadata The document to save
     * @return Saved document
     */
    public DocumentMetadata saveDocument(DocumentMetadata documentMetadata) {
        return documentRepository.save(documentMetadata);
    }

    /**
     * Mark document as failed with status
     * @param documentId The document ID
     * @param errorMessage Optional error message
     */
    public void markAsFailedWithError(String documentId, String errorMessage) {
        documentRepository.findById(documentId).ifPresent(doc -> {
            doc.setDocumentStatus(DocumentStatus.PARSING_FAILED);
            documentRepository.save(doc);
        });
    }
}
