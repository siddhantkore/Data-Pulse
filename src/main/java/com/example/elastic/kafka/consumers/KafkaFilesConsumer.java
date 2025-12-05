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
    public void consumeFile(@Header(KafkaHeaders.RECEIVED_KEY) String fileName,
                            @Payload byte[] fileBytes) throws IOException {

        MultipartFile multipartFile = new InMemoryMultipartFile(fileName, fileName, "application/octet-stream", fileBytes);

//        documentProcessorService.processAndStore(multipartFile);

        System.out.println("✅ Multipart reconstructed: " + multipartFile.getOriginalFilename());
    }


}
