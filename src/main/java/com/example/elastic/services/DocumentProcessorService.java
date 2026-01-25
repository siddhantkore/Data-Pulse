package com.example.elastic.services;

import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.models.enums.DocumentStatus;
import com.example.elastic.services.extractors.UniversalTextExtractor;
import com.example.elastic.services.processors.DocumentDataMapper;
import com.example.elastic.services.processors.DocumentPersistenceService;
import com.example.elastic.services.processors.LlmProcessingService;
import com.example.elastic.services.processors.TextCleaningService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

/**
 * Orchestrator service that coordinates document processing workflow.
 * Delegates specific tasks to specialized services following SOLID principles.
 */
@Service
public class DocumentProcessorService {

    private final UniversalTextExtractor textExtractor;
    private final TextCleaningService textCleaningService;
    private final LlmProcessingService llmProcessingService;
    private final DocumentDataMapper documentDataMapper;
    private final DocumentPersistenceService documentPersistenceService;

    public DocumentProcessorService(
            UniversalTextExtractor textExtractor,
            TextCleaningService textCleaningService,
            LlmProcessingService llmProcessingService,
            DocumentDataMapper documentDataMapper,
            DocumentPersistenceService documentPersistenceService
    ) {
        this.textExtractor = textExtractor;
        this.textCleaningService = textCleaningService;
        this.llmProcessingService = llmProcessingService;
        this.documentDataMapper = documentDataMapper;
        this.documentPersistenceService = documentPersistenceService;
    }

    /**
     * Creates a pending document entry in database.
     *
     * @param documentMetadata the document metadata to create
     * @return saved document with generated ID
     */
    public DocumentMetadata createPendingDocument(DocumentMetadata documentMetadata) {
        return documentPersistenceService.createPendingDocument(documentMetadata);
    }

    /**
     * Main async processing pipeline for documents from Kafka queue.
     * Processing steps:
     * 1. Update status to PROCESSING
     * 2. Extract text using appropriate strategy
     * 3. Clean extracted text
     * 4. Process with LLM
     * 5. Map response to document model
     * 6. Save to database
     *
     * @param fileBytes the file content as byte array
     * @param documentId the document ID
     * @param fileName the original filename
     * @return processed document metadata, or null if processing failed
     */
    public DocumentMetadata processAndStore(byte[] fileBytes, String documentId, String fileName) {
        try {
            DocumentMetadata document = documentPersistenceService.getDocumentById(documentId)
                    .orElseGet(DocumentMetadata::new);

            documentPersistenceService.updateDocumentStatus(documentId, DocumentStatus.PROCESSING);
            System.out.println("Processing document: " + documentId);

            String extractedText = textExtractor.extractText(fileBytes, fileName);
            System.out.println("Text extraction completed for: " + documentId);

            String cleanedText = textCleaningService.cleanText(extractedText);

            System.out.println("Processing with LLM for document: " + documentId);
            String llmResponse = llmProcessingService.processWithLLM(cleanedText);

            document = documentDataMapper.mapLLMResponseToDocument(llmResponse, document);
            document.setDocumentStatus(DocumentStatus.PROCESSED_OK);

            document.setDocumentStatus(DocumentStatus.SAVED_TO_DB);
            document = documentPersistenceService.saveDocument(document);
            System.out.println("Document processed successfully: " + documentId);

            return document;

        } catch (JsonProcessingException e) {
            System.err.println("JSON Mapping Error for " + documentId + ": " + e.getMessage());
            handleProcessingError(documentId, e);
            return null;
        } catch (Exception e) {
            System.err.println("Error processing document " + documentId + ": " + e.getMessage());
            e.printStackTrace();
            handleProcessingError(documentId, e);
            return null;
        }
    }

    /**
     * Gets document processing status.
     *
     * @param documentId the document ID
     * @return document metadata, or null if not found
     */
    public DocumentMetadata getDocumentStatus(String documentId) {
        return documentPersistenceService.getDocumentById(documentId).orElse(null);
    }

    /**
     * Marks document as failed with error message.
     *
     * @param documentId the document ID
     * @param errorMessage the error description
     */
    public void markDocumentAsFailed(String documentId, String errorMessage) {
        documentPersistenceService.markAsFailedWithError(documentId, errorMessage);
    }

    /**
     * Handles processing errors by updating document status.
     *
     * @param documentId the document ID
     * @param exception the exception that occurred
     */
    private void handleProcessingError(String documentId, Exception exception) {
        try {
            documentPersistenceService.updateDocumentStatus(documentId, DocumentStatus.PARSING_FAILED);
        } catch (Exception ex) {
            System.err.println("Failed to update document status: " + ex.getMessage());
        }
    }
}
