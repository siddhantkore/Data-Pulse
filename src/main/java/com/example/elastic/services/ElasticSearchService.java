package com.example.elastic.services;

import com.example.elastic.models.DocumentMetadata;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

/**
 * Elasticsearch search service providing text search and document retrieval.
 * Delegates to OptimizedSearchService for enhanced search capabilities.
 */
@Service
public class ElasticSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final OptimizedSearchService optimizedSearchService;

    public ElasticSearchService(
            ElasticsearchOperations elasticsearchOperations,
            OptimizedSearchService optimizedSearchService) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.optimizedSearchService = optimizedSearchService;
    }

    /**
     * Search documents where extractedText contains the query string.
     * Uses OptimizedSearchService for enhanced full-text search.
     */
    public List<DocumentMetadata> searchByText(String query) {
        return optimizedSearchService.findText(query);
    }

    /**
     * Search by file name.
     *
     * @param fileName the file name to search for
     * @return list of matching documents
     */
    public List<DocumentMetadata> searchByFileName(String fileName) {
        return optimizedSearchService.searchByFileName(fileName);
    }

    /**
     * Full text search with fuzzy matching across multiple fields.
     * Automatically handles typos and partial matches.
     */
    public List<DocumentMetadata> fullTextSearch(String query) {
        return optimizedSearchService.fullTextSearch(query);
    }

    /**
     * Full text search with pagination support.
     *
     * @param query search query
     * @param pageable pagination parameters
     * @return page of search results
     */
    public Page<DocumentMetadata> fullTextSearchPaginated(String query, Pageable pageable) {
        try {
            NativeQuery searchQuery = NativeQuery.builder()
                    .withPageable(pageable)
                    .withQuery(q -> q
                            .multiMatch(m -> m
                                    .fields("title^3", "fileName^2", "extractedText", "keywords")
                                    .query(query)
                                    .fuzziness("AUTO")
                            )
                    )
                    .build();

            SearchHits<DocumentMetadata> searchHits = elasticsearchOperations
                    .search(searchQuery, DocumentMetadata.class);
            List<DocumentMetadata> content = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .toList();
            return new PageImpl<>(content, pageable, searchHits.getTotalHits());
        } catch (Exception e) {
            e.printStackTrace();
            return new PageImpl<>(List.of(), pageable, 0);
        }
    }
}

