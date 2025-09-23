package com.example.elastic.repository;

import com.example.elastic.model.DocumentMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentMongoRepository extends MongoRepository<DocumentMetadata, String> {

}
