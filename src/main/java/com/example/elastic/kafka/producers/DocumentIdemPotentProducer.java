package com.example.elastic.kafka.producers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service for sending files to Kafka topic for asynchronous processing.
 */
@Service
public class DocumentIdemPotentProducer {

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    /**
     * Sends a multipart file to Kafka queue with document metadata.
     *
     * @param file the file to send
     * @param documentId the unique document identifier
     * @throws IOException if file reading fails
     */
    public void sendFile(MultipartFile file, String documentId) throws IOException {
        kafkaTemplate.send(topic, documentId, file.getBytes());
    }

    /**
     * Sends raw bytes to Kafka queue with document metadata.
     *
     * @param fileBytes the file content as bytes
     * @param documentId the unique document identifier
     * @param fileName the original filename
     * @throws IOException if sending fails
     */
    public void sendFileBytes(byte[] fileBytes, String documentId, String fileName) throws IOException {
        kafkaTemplate.send(topic, documentId, fileBytes);
    }
}
