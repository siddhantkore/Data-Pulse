package com.example.elastic.services.extractors.strategy;

import com.example.elastic.models.enums.FileType;

/**
 * Strategy interface for text extraction from different file types.
 * Implementations provide specialized extraction logic for specific file formats.
 */
public interface TextExtractorStrategy {

    /**
     * Extracts text content from file bytes.
     *
     * @param fileBytes the file content as byte array
     * @param fileName the original filename (for context)
     * @return extracted text content
     * @throws Exception if extraction fails
     */
    String extractText(byte[] fileBytes, String fileName) throws Exception;

    /**
     * Checks if this extractor supports the given file type.
     *
     * @param fileType the file type to check
     * @return true if supported, false otherwise
     */
    boolean supports(FileType fileType);
}
