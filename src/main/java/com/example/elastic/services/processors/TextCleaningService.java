package com.example.elastic.services.processors;

import org.springframework.stereotype.Service;

/**
 * Handles text cleaning and normalization.
 * Single Responsibility: Normalize and clean extracted text
 */
@Service
public class TextCleaningService {

    /**
     * Clean and normalize text extracted from OCR
     * - Normalize line breaks
     * - Collapse multiple spaces
     * - Trim whitespace
     * @param rawText The raw text from OCR
     * @return Cleaned text
     */
    public String cleanText(String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return "";
        }

        return rawText
                .replaceAll("[\\n\\r]+", "\n")   // normalize line breaks
                .replaceAll("\\s{2,}", " ")      // collapse extra spaces
                .trim();
    }

    /**
     * Sanitize LLM response by removing markdown code blocks
     * @param llmResponse The raw LLM response
     * @return Sanitized JSON string
     */
    public String sanitizeLlmResponse(String llmResponse) {
        if (llmResponse == null || llmResponse.isEmpty()) {
            return "{}";
        }

        return llmResponse
                .trim()
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
    }
}
