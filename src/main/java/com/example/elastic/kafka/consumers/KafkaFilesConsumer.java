package com.example.elastic.kafka.consumers;

import com.example.elastic.services.DocumentProcessorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Kafka consumer for processing documents from async queue.
 */
@Service
public class KafkaFilesConsumer {

    private final DocumentProcessorService documentProcessorService;

    public KafkaFilesConsumer(DocumentProcessorService documentProcessorService) {
        this.documentProcessorService = documentProcessorService;
    }

    /**
     * Consumes and processes files from Kafka topic.
     *
     * @param documentId the document identifier from message key
     * @param fileName the original filename from header
     * @param fileBytes the file content as bytes
     * @throws IOException if file reading fails
     */
    @KafkaListener(topics = "${app.kafka.topic}", groupId = "file-consumer")
    public void consumeFile(
            @Header(KafkaHeaders.RECEIVED_KEY) String documentId,
            @Header(name = "fileName", required = false) String fileName,
            @Payload byte[] fileBytes) throws IOException {

        try {
            documentProcessorService.processAndStore(fileBytes, documentId, fileName);
            System.out.println("Document processed successfully: " + documentId);
        } catch (Exception e) {
            System.err.println("Error processing document: " + documentId);
            e.printStackTrace();
            documentProcessorService.markDocumentAsFailed(documentId, e.getMessage());
        }
    }
}
