package com.example.elastic.services;

import com.example.elastic.models.DocumentMetadata;
import java.util.List;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

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
     *
     * @param query the search query string
     * @return list of matching documents
     */
    public List<DocumentMetadata> searchByText(String query) {
        return null; // searchRepository.findByExtractedTextContainingIgnoreCase(query);
    }

    /**
     * Search by file name.
     *
     * @param fileName the file name to search for
     * @return list of matching documents
     */
    public List<DocumentMetadata> searchByFileName(String fileName) {
        return null; //searchRepository.findByFileNameContainingIgnoreCase(fileName);
    }

    /**
     * Full text search
     *
     * @param query the search query string
     * @return list of matching documents
     * @throws Exception if search fails
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
