package com.example.elastic.services;

import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.repository.DocumentMongoRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MongoSearchService {

    private final DocumentMongoRepository documentMongoRepository;

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

    /**
     * Get all unique categories with their document counts
     * @return Map of category name to document count
     */
    public Map<String, Long> getCategoriesWithCounts() {
        try {
            List<DocumentMetadata> allDocuments = documentMongoRepository.findAll();
            Map<String, Long> categoryCounts = new HashMap<>();
            
            for (DocumentMetadata doc : allDocuments) {
                if (doc.getCategories() != null) {
                    for (String category : doc.getCategories()) {
                        categoryCounts.put(category, categoryCounts.getOrDefault(category, 0L) + 1);
                    }
                }
            }
            
            return categoryCounts;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
