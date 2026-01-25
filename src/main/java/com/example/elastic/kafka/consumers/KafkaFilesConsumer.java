package com.example.elastic.kafka.consumers;

import com.example.elastic.services.DocumentProcessorService;
import com.example.elastic.utils.InMemoryMultipartFile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class KafkaFilesConsumer {

    private final DocumentProcessorService documentProcessorService;

    public KafkaFilesConsumer(DocumentProcessorService documentProcessorService) {
        this.documentProcessorService = documentProcessorService;
    }

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "file-consumer")
    public void consumeFile(@Header(KafkaHeaders.RECEIVED_KEY) String documentId,
                            @Payload byte[] fileBytes) throws IOException {

        try {
            documentProcessorService.processAndStore(fileBytes, documentId);
            System.out.println("Document processed successfully: " + documentId);
        } catch (Exception e) {
            System.err.println("Error processing document: " + documentId);
            e.printStackTrace();
            // Update document status to FAILED
            documentProcessorService.markDocumentAsFailed(documentId, e.getMessage());
        }
    }


}
