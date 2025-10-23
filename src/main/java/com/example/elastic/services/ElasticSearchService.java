package com.example.elastic.services;

import com.example.elastic.models.DocumentMetadata;
//import com.example.elastic.repository.DocumentSearchRepository;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElasticSearchService {
//    private final DocumentSearchRepository searchRepository;

    private final ElasticsearchOperations elasticsearchOperations;

    public ElasticSearchService(/*DocumentSearchRepository searchRepository,*/ ElasticsearchOperations elasticsearchOperations) {
//        this.searchRepository = searchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    /**
     * Search documents where extractedText contains the query string.
     */
    public List<DocumentMetadata> searchByText(String query) {
        return null; // searchRepository.findByExtractedTextContainingIgnoreCase(query);
    }

    /**
     * Search by file name.
     */
    public List<DocumentMetadata> searchByFileName(String fileName) {
        return null ; //searchRepository.findByFileNameContainingIgnoreCase(fileName);
    }

    /**
     * Full text search
     */
    public List<DocumentMetadata> fullTextSearch(String query) throws Exception {
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .fields("extractedText", "fileName")
                                .query(query)
                                .fuzziness("AUTO")   // fuzzy matching
                        )
                )
                .build();

        return elasticsearchOperations
                .search(searchQuery, DocumentMetadata.class)
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }
}
