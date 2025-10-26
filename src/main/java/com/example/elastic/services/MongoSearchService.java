package com.example.elastic.services;

import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.repository.DocumentMongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoSearchService {

    private final DocumentMongoRepository documentMongoRepository;

    public MongoSearchService(DocumentMongoRepository documentMongoRepository) {
        this.documentMongoRepository = documentMongoRepository;
    }

    /**
     *
     * @param pageable to get result in paged form
     * @return pages of DocumentMetadata Entity
     */
    public Page<DocumentMetadata> getAllDocuments(Pageable pageable){
        try {
            return documentMongoRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param text to search in Title, Dept or Keyword
     * @return List of DocumentMetadata Entity
     */
    public List<DocumentMetadata> findText(String text) {
        try {
            return documentMongoRepository.searchByTitleDeptOrKeyword(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
