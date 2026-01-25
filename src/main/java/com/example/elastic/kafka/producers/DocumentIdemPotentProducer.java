package com.example.elastic.kafka.producers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// Produce docs to queue and set status

@Service
public class DocumentIdemPotentProducer {
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void sendFile(MultipartFile file, String documentId) throws IOException {
        kafkaTemplate.send(
                topic,
                documentId,
                file.getBytes()
        );
    }

    public void sendFileBytes(byte[] fileBytes, String documentId) throws IOException {
        kafkaTemplate.send(
                topic,
                documentId,
                fileBytes
        );
    }
}
