package com.example.elastic.repository;

import com.example.elastic.models.DocumentMetadata;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @deprecated Use {@link DocumentElasticsearchRepository} instead.
 * DocumentMongoRepository is deprecated as the application has migrated from MongoDB
 * to Elasticsearch as the primary data store. All document persistence operations
 * are now handled by DocumentElasticsearchRepository.
 *
 * Migration steps:
 * 1. Replace DocumentMongoRepository injections with DocumentElasticsearchRepository
 * 2. Update queries to use Elasticsearch query DSL
 * 3. Test thoroughly with actual Elasticsearch instance
 * 4. Remove this interface in next major release
 *
 * This interface will be removed in a future version.
 */
@Deprecated(since = "10.0", forRemoval = true)
@Repository
public interface DocumentMongoRepository extends MongoRepository<DocumentMetadata, String> {

    /**
     * @deprecated Use {@link DocumentElasticsearchRepository#findByFileNameContainingIgnoreCase(String)}
     * or {@link DocumentElasticsearchRepository#findByCategories(String)} instead.
     */
    @Deprecated(since = "10.0", forRemoval = true)
    @Query("{ '$or': [ " +
            " { 'metadata.title': { $regex: ?0, $options: 'i' } }, " +
            " { 'documentSpecificFields.dept': { $regex: ?0, $options: 'i' } }, " +
            " { 'keywords': { $in: [?0] } } " +
            "] }")
    List<DocumentMetadata> searchByTitleDeptOrKeyword(String searchTerm);

    @NotNull Page<DocumentMetadata> findAll(@NotNull Pageable pageable);
}
