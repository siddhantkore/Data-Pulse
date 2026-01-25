package com.example.elastic.services.extractors;

import com.example.elastic.models.enums.FileType;
import com.example.elastic.services.extractors.strategy.TextExtractorStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Universal text extractor that routes extraction requests to appropriate strategy.
 * Uses factory pattern to select the correct extractor based on file type.
 */
@Service
public class UniversalTextExtractor {

    private final List<TextExtractorStrategy> extractors;
    private final FileTypeDetector fileTypeDetector;

    public UniversalTextExtractor(
            List<TextExtractorStrategy> extractors,
            FileTypeDetector fileTypeDetector
    ) {
        this.extractors = extractors;
        this.fileTypeDetector = fileTypeDetector;
    }

    /**
     * Extracts text from file bytes using appropriate extraction strategy.
     *
     * @param fileBytes the file content as byte array
     * @param fileName the original filename
     * @return extracted text content
     * @throws Exception if no suitable extractor found or extraction fails
     */
    public String extractText(byte[] fileBytes, String fileName) throws Exception {
        FileType fileType = fileTypeDetector.detectFileType(fileBytes, fileName);
        
        TextExtractorStrategy extractor = findExtractorForType(fileType);
        if (extractor == null) {
            throw new UnsupportedOperationException(
                "No extractor available for file type: " + fileType
            );
        }

        return extractor.extractText(fileBytes, fileName);
    }

    /**
     * Finds the appropriate extractor strategy for the given file type.
     *
     * @param fileType the file type to extract
     * @return matching extractor strategy, or null if none found
     */
    private TextExtractorStrategy findExtractorForType(FileType fileType) {
        return extractors.stream()
                .filter(extractor -> extractor.supports(fileType))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if a file type is supported for extraction.
     *
     * @param fileBytes the file content
     * @param fileName the filename
     * @return true if supported, false otherwise
     */
    public boolean isSupported(byte[] fileBytes, String fileName) {
        FileType fileType = fileTypeDetector.detectFileType(fileBytes, fileName);
        return findExtractorForType(fileType) != null;
    }
}
