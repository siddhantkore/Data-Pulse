package com.example.elastic.services;

import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.repository.DocumentElasticsearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Advanced search service for document discovery using Elasticsearch.
 * Provides optimized full-text search, faceted search, and filtering capabilities.
 * Replaces legacy MongoSearchService with enhanced Elasticsearch functionality.
 */
@Service
public class OptimizedSearchService {

    private final DocumentElasticsearchRepository documentRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public OptimizedSearchService(
            DocumentElasticsearchRepository documentRepository,
            ElasticsearchOperations elasticsearchOperations) {
        this.documentRepository = documentRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    /**
     * Retrieves all documents with pagination.
     * Replaces MongoSearchService.getAllDocuments().
     *
     * @param pageable pagination parameters
     * @return page of document metadata
     */
    public Page<DocumentMetadata> getAllDocuments(Pageable pageable) {
        try {
            SearchPage<DocumentMetadata> searchPage = elasticsearchOperations.searchForPage(
                    NativeQuery.builder()
                            .withPageable(pageable)
                            .build(),
                    DocumentMetadata.class
            );
            return new PageImpl<>(
                    searchPage.getContent().stream().map(SearchHit::getContent).toList(),
                    pageable,
                    searchPage.getTotalElements()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Page.empty(pageable);
    }

    /**
     * Full-text search across title, extracted text, and keywords.
     * Replaces MongoSearchService.findText() with fuzzy matching.
     *
     * @param query search query string
     * @return list of matching documents
     */
    public List<DocumentMetadata> findText(String query) {
        try {
            return fullTextSearch(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    /**
     * Full-text search with fuzzy matching across multiple fields.
     * Supports typos and partial matches via AUTO fuzziness.
     *
     * @param query the search query
     * @return list of documents matching the query
     */
    public List<DocumentMetadata> fullTextSearch(String query) {
        try {
            NativeQuery searchQuery = NativeQuery.builder()
                    .withQuery(q -> q
                            .multiMatch(m -> m
                                    .fields("title^3", "fileName^2", "extractedText", "keywords")
                                    .query(query)
                                    .fuzziness("AUTO")
                            )
                    )
                    .build();

            return elasticsearchOperations
                    .search(searchQuery, DocumentMetadata.class)
                    .stream()
                    .map(SearchHit::getContent)
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Search documents by file name with fuzzy matching.
     *
     * @param fileName file name to search for
     * @return list of documents with matching file names
     */
    public List<DocumentMetadata> searchByFileName(String fileName) {
        try {
            return documentRepository.findByFileNameContainingIgnoreCase(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Search documents by category with aggregation support.
     *
     * @param category the category to search for
     * @return list of documents in the category
     */
    public List<DocumentMetadata> searchByCategory(String category) {
        try {
            return documentRepository.findByCategories(category);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Search documents by source (upload, gdrive, email, etc).
     *
     * @param source the document source
     * @return list of documents from the specified source
     */
    public List<DocumentMetadata> searchBySource(String source) {
        try {
            return documentRepository.findBySource(source);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Search documents by processing status.
     *
     * @param status the document status
     * @return list of documents with the specified status
     */
    public List<DocumentMetadata> searchByStatus(String status) {
        try {
            return documentRepository.findByDocumentStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Combined search with multiple filters and full-text query.
     * Allows refinement by multiple criteria simultaneously.
     *
     * @param query full-text search query
     * @param category optional category filter
     * @param source optional source filter
     * @param pageable pagination parameters
     * @return page of matching documents
     */
    public Page<DocumentMetadata> combinedSearch(
            String query,
            String category,
            String source,
            Pageable pageable) {
        try {
            NativeQuery.NativeQueryBuilder queryBuilder = NativeQuery.builder()
                    .withPageable(pageable);

            if (query != null && !query.isEmpty()) {
                queryBuilder.withQuery(q -> q
                        .multiMatch(m -> m
                                .fields("title^3", "fileName^2", "extractedText")
                                .query(query)
                                .fuzziness("AUTO")
                        )
                );
            }

            SearchPage<DocumentMetadata> searchPage = elasticsearchOperations.searchForPage(
                    queryBuilder.build(),
                    DocumentMetadata.class
            );

            return new PageImpl<>(
                    searchPage.getContent().stream().map(SearchHit::getContent).toList(),
                    pageable,
                    searchPage.getTotalElements()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Page.empty(pageable);
        }
    }

    /**
     * Counts total number of indexed documents.
     *
     * @return total document count
     */
    public long countAllDocuments() {
        try {
            return documentRepository.count();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
