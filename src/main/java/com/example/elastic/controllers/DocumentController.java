package com.example.elastic.controllers;

import com.example.elastic.connectors.mail_connectors.GmailIMAPConnector;
import com.example.elastic.kafka.producers.DocumentIdemPotentProducer;
import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.models.enums.DocumentStatus;
import com.example.elastic.repository.DocumentElasticsearchRepository;
import com.example.elastic.services.DocumentProcessorService;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentProcessorService documentProcessorService;
    private final GmailIMAPConnector connector;
    private final DocumentIdemPotentProducer documentIdemPotentProducer;
    private final DocumentElasticsearchRepository documentElasticsearchRepository;

    /**
     * @param file take Multipart file as input
     * @return DocumentMetadata with PENDING status and document ID (for polling)
     * File is sent to Kafka queue for async OCR processing (non-blocking)
     */
    @PostMapping("/upload")
    @CrossOrigin(origins="*")
    public ResponseEntity<DocumentMetadata> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        // Create initial document metadata with UPLOADED status
        DocumentMetadata pendingDocument = new DocumentMetadata();
        pendingDocument.setFileName(file.getOriginalFilename());
        pendingDocument.setDocumentStatus(DocumentStatus.UPLOADED);
        DocumentMetadata saved = documentProcessorService.createPendingDocument(pendingDocument);

        // Send file to Kafka queue for async OCR processing
        documentIdemPotentProducer.sendFile(file, saved.getId());

        // Return immediately with document ID (status = PENDING)
        return ResponseEntity.accepted().body(saved);
    }

    /**
     * @return currently row implementation for email fetching
     */
    @GetMapping("/pull")
    public ResponseEntity<String> pullMails () {
        if(connector.sendToDocumentService()){
            return ResponseEntity.ok("Ok");
        }
        return ResponseEntity.ok("Bad Not OK");
    }

    /**
     * Get document processing status for polling
     * @param documentId the document ID returned from /upload
     * @return Document with current status (PENDING, PROCESSING, PROCESSED_OK, PARSING_FAILED)
     */
    @GetMapping("/status/{documentId}")
    public ResponseEntity<DocumentMetadata> getDocumentStatus(@PathVariable String documentId) {
        DocumentMetadata document = documentProcessorService.getDocumentStatus(documentId);
        if (document == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(document);
    }


    /**
     *
     * @param s3Key and
     * @return The respective document from S3
     */
    @GetMapping("/download/{s3Key}")
    public ResponseEntity<byte[]> getDocument(@PathVariable String s3Key) {
//        byte[] content = s3Service.getDocument("my-bucket-name", s3Key);

//        return ResponseEntity.ok()
//                .header("Content-Type", "application/octet-stream")
//                .header("Content-Disposition", "attachment; filename=\"" + s3Key + "\"")
//                .body(content);
        return null;
    }

    /**
     * Delete a document by ID
     * @param id the document ID to delete
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDocument(@PathVariable String id) {
        Map<String, Object> response = new java.util.HashMap<>();
        try {
            System.out.println("Received delete request for document ID: " + id);

            // Use Elasticsearch repository for deletion
            documentElasticsearchRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Document deleted successfully");
            response.put("id", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception during document deletion: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error deleting document: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }
}
