package com.example.elastic.services.processors;

import com.example.elastic.services.llm.LLMService;
import org.springframework.stereotype.Service;

/**
 * Handles LLM (Large Language Model) processing of text.
 * Single Responsibility: Process cleaned text with LLM to extract structured data
 */
@Service
public class LlmProcessingService {

    private final LLMService llmService;
    private final TextCleaningService textCleaningService;

    public LlmProcessingService(LLMService llmService, TextCleaningService textCleaningService) {
        this.llmService = llmService;
        this.textCleaningService = textCleaningService;
    }

    /**
     * Process cleaned text with LLM for extraction and categorization
     * @param cleanedText The cleaned extracted text
     * @return Sanitized JSON response from LLM
     */
    public String processWithLLM(String cleanedText) {
        String rawResponse = llmService.processWithOpenAPI(cleanedText);
        return textCleaningService.sanitizeLlmResponse(rawResponse);
    }
}
