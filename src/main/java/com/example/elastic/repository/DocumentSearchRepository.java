//package com.example.elastic.repository;
//
//import com.example.elastic.models.DocumentMetadata;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface DocumentSearchRepository extends ElasticsearchRepository<DocumentMetadata, String> {
//    List<DocumentMetadata> findByExtractedTextContainingIgnoreCase(String text);
//
//    List<DocumentMetadata> findByFileNameContainingIgnoreCase(String fileName);
//}
