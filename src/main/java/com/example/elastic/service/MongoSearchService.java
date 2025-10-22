package com.example.elastic.service;

import com.example.elastic.model.DocumentMetadata;
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

    public Page<DocumentMetadata> getAllDocuments(Pageable pageable){
        try {
            return documentMongoRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DocumentMetadata> findText(String text) {
        try {
            return documentMongoRepository.searchByTitleDeptOrKeyword(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
