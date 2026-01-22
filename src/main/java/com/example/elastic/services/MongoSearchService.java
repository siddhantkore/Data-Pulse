package com.example.elastic.services;

import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.repository.DocumentMongoRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            Page<DocumentMetadata> result = documentMongoRepository.findAll(pageable);
            if(result != null) {
                return result;
            }
            return org.springframework.data.domain.Page.empty(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.data.domain.Page.empty(pageable);
        }
    }

    /**
     *
     * @param text to search in Title, Dept or Keyword
     * @return List of DocumentMetadata Entity
     */
    public List<DocumentMetadata> findText(String text) {
        try {
            List<DocumentMetadata> results = documentMongoRepository.searchByTitleDeptOrKeyword(text);
            if(results != null) {
                return results;
            }
            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
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

    /**
     * Delete a document by ID
     * @param id the document ID to delete
     * @return true if document was deleted successfully, false otherwise
     */
    public boolean deleteDocumentById(String id) {
        try {
            System.out.println("Attempting to delete document with ID: " + id);
            
            // Check if document exists
            if (!documentMongoRepository.existsById(id)) {
                System.out.println("Document not found with ID: " + id);
                return false;
            }

            // Delete the document
            documentMongoRepository.deleteById(id);
            System.out.println("Delete operation completed for ID: " + id);

            // Verify deletion
//            boolean stillExists = documentMongoRepository.existsById(id);
//            if (stillExists) {
//                System.out.println("WARNING: Document still exists after deletion attempt for ID: " + id);
//                return false;
//            }

            System.out.println("Document successfully deleted with ID: " + id);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting document with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
