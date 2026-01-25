package com.example.elastic.services;

import com.example.elastic.models.DocumentMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @deprecated Use {@link OptimizedSearchService} instead.
 * MongoSearchService is deprecated as the application has migrated from MongoDB
 * to Elasticsearch as the primary database. All search operations are now
 * performed using OptimizedSearchService for better performance and features.
 *
 * Migration path:
 * - findText(query) -> use OptimizedSearchService.findText(query)
 * - getAllDocuments(pageable) -> use OptimizedSearchService.getAllDocuments(pageable)
 *
 * This class will be removed in a future version.
 */
@Deprecated(since = "10.0", forRemoval = true)
@Service
public class MongoSearchService {

    private final OptimizedSearchService optimizedSearchService;

    public MongoSearchService(OptimizedSearchService optimizedSearchService) {
        this.optimizedSearchService = optimizedSearchService;
    }

    /**
     * @deprecated Use {@link OptimizedSearchService#getAllDocuments(Pageable)} instead.
     *
     * @param pageable to get result in paged form
     * @return pages of DocumentMetadata Entity
     */
    @Deprecated(since = "10.0", forRemoval = true)
    public Page<DocumentMetadata> getAllDocuments(Pageable pageable) {
        return optimizedSearchService.getAllDocuments(pageable);
    }

    /**
     * @deprecated Use {@link OptimizedSearchService#findText(String)} instead.
     *
     * @param text to search in Title, Dept or Keyword
     * @return List of DocumentMetadata Entity
     */
    @Deprecated(since = "10.0", forRemoval = true)
    public List<DocumentMetadata> findText(String text) {
        return optimizedSearchService.findText(text);
    }
}
