package com.example.elastic.services;

import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.models.enums.DocumentStatus;
import com.example.elastic.services.processors.DocumentDataMapper;
import com.example.elastic.services.processors.DocumentPersistenceService;
import com.example.elastic.services.processors.FileHandlingService;
import com.example.elastic.services.processors.LlmProcessingService;
import com.example.elastic.services.processors.OcrExtractionService;
import com.example.elastic.services.processors.TextCleaningService;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Orchestrator service that coordinates document processing workflow.
 * Single Responsibility: Orchestrate the document processing pipeline
 * Delegates specific tasks to specialized services following SOLID principles
 */
@Service
public class DocumentProcessorService {

    private final OcrExtractionService ocrService;
    private final TextCleaningService textCleaningService;
    private final LlmProcessingService llmProcessingService;
    private final DocumentDataMapper documentDataMapper;
    private final DocumentPersistenceService documentPersistenceService;

    public DocumentProcessorService(
            OcrExtractionService ocrService,
            TextCleaningService textCleaningService,
            LlmProcessingService llmProcessingService,
            DocumentDataMapper documentDataMapper,
            DocumentPersistenceService documentPersistenceService
    ) {
        this.ocrService = ocrService;
        this.textCleaningService = textCleaningService;
        this.llmProcessingService = llmProcessingService;
        this.documentDataMapper = documentDataMapper;
        this.documentPersistenceService = documentPersistenceService;
    }

    /**
     * Create a pending document entry in database
     */
    public DocumentMetadata createPendingDocument(DocumentMetadata documentMetadata) {
        return documentPersistenceService.createPendingDocument(documentMetadata);
    }

    /**
     * Main async processing pipeline for documents from Kafka queue
     * Steps:
     * 1. Update status to PROCESSING
     * 2. Extract text via OCR
     * 3. Clean extracted text
     * 4. Process with LLM
     * 5. Map response to document model
     * 6. Save to database
     * 7. Cleanup temp files
     */
    public DocumentMetadata processAndStore(byte[] fileBytes, String documentId) {
        File tempFile = null;
        try {
            // Step 0: Retrieve existing document
            DocumentMetadata document = documentPersistenceService.getDocumentById(documentId)
                    .orElseGet(DocumentMetadata::new);

            // Step 1: Update status to PROCESSING
            documentPersistenceService.updateDocumentStatus(documentId, DocumentStatus.PROCESSING);
            System.out.println("Processing document: " + documentId);

            // Step 2: Create temp file from bytes and extract text via OCR
            tempFile = FileHandlingService.createTempFileFromBytes(documentId, fileBytes);
            System.out.println("Starting OCR for document: " + documentId);
            String extractedText = ocrService.extractText(tempFile);

            // Step 3: Clean extracted text
            String cleanedText = textCleaningService.cleanText(extractedText);

            // Step 4: Process with LLM
            System.out.println("Processing with LLM for document: " + documentId);
            String llmResponse = llmProcessingService.processWithLLM(cleanedText);

            // Step 5: Map LLM response to document
            document = documentDataMapper.mapLLMResponseToDocument(llmResponse, document);
            document.setDocumentStatus(DocumentStatus.PROCESSED_OK);

            // Step 6: Save to database
            document.setDocumentStatus(DocumentStatus.SAVED_TO_DB);
            document = documentPersistenceService.saveDocument(document);
            System.out.println("Document processed successfully: " + documentId);

            return document;

        } catch (TesseractException e) {
            System.err.println("OCR Error processing document " + documentId + ": " + e.getMessage());
            handleProcessingError(documentId, e);
            return null;
        } catch (JsonProcessingException e) {
            System.err.println("JSON Mapping Error processing document " + documentId + ": " + e.getMessage());
            handleProcessingError(documentId, e);
            return null;
        } catch (IOException e) {
            System.err.println("File I/O Error processing document " + documentId + ": " + e.getMessage());
            handleProcessingError(documentId, e);
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error processing document " + documentId + ": " + e.getMessage());
            e.printStackTrace();
            handleProcessingError(documentId, e);
            return null;
        } finally {
            // Cleanup temp file
            FileHandlingService.deleteTempFile(tempFile);
        }
    }

    /**
     * Get document processing status
     */
    public DocumentMetadata getDocumentStatus(String documentId) {
        return documentPersistenceService.getDocumentById(documentId).orElse(null);
    }

    /**
     * Mark document as failed
     */
    public void markDocumentAsFailed(String documentId, String errorMessage) {
        documentPersistenceService.markAsFailedWithError(documentId, errorMessage);
    }

    /**
     * Handle processing errors by updating document status
     */
    private void handleProcessingError(String documentId, Exception e) {
        try {
            documentPersistenceService.updateDocumentStatus(documentId, DocumentStatus.PARSING_FAILED);
        } catch (Exception ex) {
            System.err.println("Failed to update document status to FAILED: " + ex.getMessage());
        }
    }
}
