package com.example.elastic.controller;

import com.example.elastic.model.DocumentMetadata;
import com.example.elastic.repository.DocumentMongoRepository;
import com.example.elastic.service.ElasticSearchService;
import com.example.elastic.service.MongoSearchService;
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

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {
    // Adapt the searchController to use DocumentMongoRepository instead of ElasticSearch repository

    private final ElasticSearchService elasticSearchService;
    private final MongoSearchService mongoSearchSearvice;
    private final DocumentMongoRepository documentMongoRepository;

    public SearchController(ElasticSearchService elasticSearchService, MongoSearchService mongoSearchSearvice, DocumentMongoRepository documentMongoRepository) {
        this.elasticSearchService = elasticSearchService;
        this.mongoSearchSearvice = mongoSearchSearvice;
        this.documentMongoRepository = documentMongoRepository;
    }

    /**
     *
     * @param q text to search in mongodb
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

    @GetMapping("/getalldocs")
    public PagedModel<DocumentMetadata> getPagedDocuments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DocumentMetadata> documentMetadataPage = mongoSearchSearvice.getAllDocuments(pageable);

        return new PagedModel<>(documentMetadataPage);
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


}
