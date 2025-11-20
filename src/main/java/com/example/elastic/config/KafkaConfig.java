package com.example.elastic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfig {

    String producerConfiguration = "null";
    public KafkaTemplate<String, Object> kafkaTemplate() {
//        return new KafkaTemplate<>(producerConfiguration);
        return null;
    }
}
