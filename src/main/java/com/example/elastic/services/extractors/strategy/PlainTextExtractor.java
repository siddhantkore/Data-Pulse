package com.example.elastic.services.extractors.strategy;

import com.example.elastic.models.enums.FileType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Text extraction strategy for plain text files.
 * Supports TXT, CSV, JSON, XML and other text-based formats.
 */
@Service
public class PlainTextExtractor implements TextExtractorStrategy {

    @Override
    public String extractText(byte[] fileBytes, String fileName) {
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    @Override
    public boolean supports(FileType fileType) {
        return fileType.isPlainText();
    }
}
