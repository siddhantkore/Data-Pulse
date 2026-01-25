package com.example.elastic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main Spring Boot application class for Data-Pulse document management system.
 * Enables Elasticsearch repositories and Kafka messaging.
 * Excludes MongoDB autoconfiguration as MongoDB has been removed from the system.
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
@EnableKafka
@EnableElasticsearchRepositories
public class ElasticApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElasticApplication.class, args);
	}

}
