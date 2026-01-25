package com.example.elastic.services.extractors.strategy;

import com.example.elastic.models.enums.FileType;
import org.springframework.stereotype.Service;

/**
 * Fallback extractor for unsupported file types.
 * Throws an exception indicating the file type is not supported.
 */
@Service
public class UnsupportedTypeExtractor implements TextExtractorStrategy {

    @Override
    public String extractText(byte[] fileBytes, String fileName) throws Exception {
        throw new UnsupportedOperationException(
            "File type not supported for text extraction: " + fileName
        );
    }

    @Override
    public boolean supports(FileType fileType) {
        return fileType == FileType.UNSUPPORTED;
    }
}
