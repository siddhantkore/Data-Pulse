package com.example.elastic.controllers;

import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.services.ElasticSearchService;
import com.example.elastic.services.MongoSearchService;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {
    // Adapt the searchController to use DocumentMongoRepository instead of ElasticSearch repository

    private final ElasticSearchService elasticSearchService;
    private final MongoSearchService mongoSearchService;

    /**
     *
     * @param q text to search in mongodb
     * @return List of DocumentMetadata result of search
     */
    @GetMapping("/text-search")
    public ResponseEntity<List<DocumentMetadata>> search(@RequestParam String q) {
        try {
            List<DocumentMetadata> results =  mongoSearchService.findText(q);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }

    /**
     *
     * @param page number to see, default 0
     * @param size of records on each page, default 20 records/page
     * @return pages of DocumentMetadata
     */
    @GetMapping("/getalldocs")
    public PagedModel<DocumentMetadata> getPagedDocuments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DocumentMetadata> documentMetadataPage = mongoSearchService.getAllDocuments(pageable);

        return new PagedModel<>(documentMetadataPage);
    }

    /**
     *
     * @param query text to search
     * @return List of DocumentMetadata result of search
     */
    @Deprecated
    @GetMapping("/text")
    public ResponseEntity<List<DocumentMetadata>> searchByText(@RequestParam("q") String query) {
        List<DocumentMetadata> results = elasticSearchService.searchByText(query);
        return ResponseEntity.ok(results);
    }


}
