package com.example.elastic.controllers;

import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.services.ElasticSearchService;
import com.example.elastic.services.OptimizedSearchService;
import com.example.elastic.services.SearchResultRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for document search operations.
 * Provides full-text search, faceted search, and result ranking via Elasticsearch.
 * Migrated from MongoDB to Elasticsearch with enhanced search capabilities.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    private final ElasticSearchService elasticSearchService;
    private final OptimizedSearchService optimizedSearchService;
    private final SearchResultRankingService searchResultRankingService;

    /**
     * Searches documents using full-text search.
     * Uses Elasticsearch for fast indexed searching with fuzzy matching.
     *
     * @param q search query string
     * @return list of matching documents
     */
    @GetMapping("/text-search")
    public ResponseEntity<List<DocumentMetadata>> search(@RequestParam String q) {
        try {
            List<DocumentMetadata> results = optimizedSearchService.findText(q);
            // Optional: rank results using LLM
            results = searchResultRankingService.rankByLlmRelevance(results, q);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            System.out.println("Search error: " + e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves all documents with pagination.
     * Efficiently pages through Elasticsearch index.
     *
     * @param page zero-based page number (default: 0)
     * @param size number of records per page (default: 20)
     * @return paginated document results
     */
    @GetMapping("/get-all-docs")
    public PagedModel<DocumentMetadata> getPagedDocuments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DocumentMetadata> documentMetadataPage = optimizedSearchService.getAllDocuments(pageable);
        return new PagedModel<>(documentMetadataPage);
    }

    /**
     * Searches documents by file name.
     *
     * @param fileName file name to search for
     * @return list of matching documents
     */
    @GetMapping("/search-by-filename")
    public ResponseEntity<List<DocumentMetadata>> searchByFileName(@RequestParam String fileName) {
        try {
            List<DocumentMetadata> results = optimizedSearchService.searchByFileName(fileName);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Searches documents by category.
     *
     * @param category category to search for
     * @return list of documents in the category
     */
    @GetMapping("/search-by-category")
    public ResponseEntity<List<DocumentMetadata>> searchByCategory(@RequestParam String category) {
        try {
            List<DocumentMetadata> results = optimizedSearchService.searchByCategory(category);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Searches documents by source (upload, gdrive, email, etc).
     *
     * @param source document source
     * @return list of documents from the specified source
     */
    @GetMapping("/search-by-source")
    public ResponseEntity<List<DocumentMetadata>> searchBySource(@RequestParam String source) {
        try {
            List<DocumentMetadata> results = optimizedSearchService.searchBySource(source);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Searches documents by processing status.
     *
     * @param status document status
     * @return list of documents with the specified status
     */
    @GetMapping("/search-by-status")
    public ResponseEntity<List<DocumentMetadata>> searchByStatus(@RequestParam String status) {
        try {
            List<DocumentMetadata> results = optimizedSearchService.searchByStatus(status);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Full-text search with text parameter.
     * Legacy endpoint maintained for backward compatibility.
     *
     * @param query search query
     * @return list of matching documents
     */
    @GetMapping("/text")
    public ResponseEntity<List<DocumentMetadata>> searchByText(@RequestParam("q") String query) {
        List<DocumentMetadata> results = elasticSearchService.searchByText(query);
        return ResponseEntity.ok(results);
    }
}
