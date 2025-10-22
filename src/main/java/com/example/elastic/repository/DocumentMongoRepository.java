package com.example.elastic.repository;

import com.example.elastic.model.DocumentMetadata;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentMongoRepository extends MongoRepository<DocumentMetadata, String> {

    @Query("{ '$or': [ " +
            " { 'metadata.title': { $regex: ?0, $options: 'i' } }, " +
            " { 'documentSpecificFields.dept': { $regex: ?0, $options: 'i' } }, " +
            " { 'keywords': { $in: [?0] } } " +
            "] }")
    List<DocumentMetadata> searchByTitleDeptOrKeyword(String searchTerm);

    @NotNull Page<DocumentMetadata> findAll(@NotNull Pageable pageable);
}
