package com.example.elastic.repository;

import com.example.elastic.models.DocumentMetadata;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Elasticsearch repository for DocumentMetadata entities.
 * Provides database operations for document persistence and retrieval.
 */
@Repository
public interface DocumentElasticsearchRepository 
        extends ElasticsearchRepository<DocumentMetadata, String> {

    /**
     * Finds documents by file name (case-insensitive).
     *
     * @param fileName the file name to search for
     * @return list of matching documents
     */
    List<DocumentMetadata> findByFileNameContainingIgnoreCase(String fileName);

    /**
     * Finds documents by category.
     *
     * @param category the category to search for
     * @return list of matching documents
     */
    List<DocumentMetadata> findByCategories(String category);

    /**
     * Finds documents by source (upload, gdrive, email, etc).
     *
     * @param source the document source
     * @return list of documents from the specified source
     */
    List<DocumentMetadata> findBySource(String source);

    /**
     * Finds documents by document status.
     *
     * @param status the processing status to search for
     * @return list of documents with the specified status
     */
    List<DocumentMetadata> findByDocumentStatus(String status);
}
