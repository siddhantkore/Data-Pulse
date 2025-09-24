package com.example.elastic.controller;

import com.example.elastic.model.DocumentMetadata;
import com.example.elastic.service.ElasticSearchService;
import com.example.elastic.service.MongoSearchSearvice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {
    // Adapt the searchController to use DocumentMongoRepository instead of ElasticSearch repository

    private final ElasticSearchService elasticSearchService;
    private final MongoSearchSearvice mongoSearchSearvice;

    public SearchController(ElasticSearchService elasticSearchService, MongoSearchSearvice mongoSearchSearvice) {
        this.elasticSearchService = elasticSearchService;
        this.mongoSearchSearvice = mongoSearchSearvice;
    }

    /**
     *
     * @param q text to search in elastic-search
     * @return List of DocumentMetadata result of search
     */
    @GetMapping("/text-search")
    public ResponseEntity<List<DocumentMetadata>> search(@RequestParam String q) {
        try {
            List<DocumentMetadata> results =  mongoSearchSearvice.findText(q);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }

    /**
     *
     * @param query text to search
     * @return List of DocumentMetadata result of search
     */
    @GetMapping("/text")
    public ResponseEntity<List<DocumentMetadata>> searchByText(@RequestParam("q") String query) {
        List<DocumentMetadata> results = elasticSearchService.searchByText(query);
        return ResponseEntity.ok(results);
    }

    /**
     *
     * @return List of DocumentMetadata result of search by file
     */
    @GetMapping("/get-all-docs")
    public ResponseEntity<List<DocumentMetadata>> searchByFileName() {
        List<DocumentMetadata> results = mongoSearchSearvice.getAllDocuments();
        return ResponseEntity.ok(results);
    }
}
