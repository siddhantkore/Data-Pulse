package com.example.elastic.services.processors;

import com.example.elastic.models.DocumentMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Handles mapping of LLM response to DocumentMetadata model.
 * Single Responsibility: Map JSON response to domain model
 */
@Service
public class DocumentDataMapper {

    private final ObjectMapper objectMapper;

    public DocumentDataMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Map LLM JSON response to DocumentMetadata entity
     * @param llmResponse The JSON response from LLM
     * @param documentMetadata The document metadata object to populate
     * @return Updated DocumentMetadata with all extracted fields
     * @throws JsonProcessingException if JSON parsing fails
     */
    public DocumentMetadata mapLLMResponseToDocument(String llmResponse, DocumentMetadata documentMetadata)
            throws JsonProcessingException {

        JsonNode root = objectMapper.readTree(llmResponse);

        // Map top-level fields
        documentMetadata.setCategories(
                objectMapper.convertValue(root.get("categories"), new TypeReference<List<String>>() {})
        );
        documentMetadata.setKeywords(
                objectMapper.convertValue(root.get("keywords"), new TypeReference<List<String>>() {})
        );

        // Map nested metadata object
        DocumentMetadata.Metadata metadata = mapMetadata(root);
        documentMetadata.setMetadata(metadata);

        // Map flexible document-specific fields
        documentMetadata.setDocumentSpecificFields(
                objectMapper.convertValue(root.get("document_specific_fields"), new TypeReference<Map<String, Object>>() {})
        );

        return documentMetadata;
    }

    /**
     * Map metadata nested object from JSON
     */
    private DocumentMetadata.Metadata mapMetadata(JsonNode root) {
        DocumentMetadata.Metadata metadata = new DocumentMetadata.Metadata();
        metadata.setTitle(root.path("metadata").path("title").asText());
        metadata.setAuthorOrSender(root.path("metadata").path("author_or_sender").asText());
        metadata.setDate(root.path("metadata").path("date").asText());
        metadata.setEntities(
                objectMapper.convertValue(root.path("metadata").path("entities"), new TypeReference<List<String>>() {})
        );
        metadata.setSummary(root.path("metadata").path("summary").asText());
        return metadata;
    }
}
