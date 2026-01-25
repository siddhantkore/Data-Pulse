package com.example.elastic.repository;

/**
 * @deprecated This class has been completely removed.
 * Use {@link DocumentElasticsearchRepository} instead.
 * 
 * DocumentMongoRepository is no longer available as MongoDB has been removed
 * from the application. All document persistence operations are now handled
 * by DocumentElasticsearchRepository using Elasticsearch.
 *
 * Migration:
 * - Replace all DocumentMongoRepository injections with DocumentElasticsearchRepository
 * - Use OptimizedSearchService for search operations
 * - See MIGRATION_GUIDE.md for complete migration instructions
 *
 * This file exists only as a marker and will be deleted in the next release.
 */
@Deprecated(since = "10.0", forRemoval = true)
public interface DocumentMongoRepository {
    // This interface is intentionally empty.
    // MongoDB has been removed from the project.
    // Use DocumentElasticsearchRepository instead.
}
