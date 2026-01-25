package com.example.elastic.services.extractors.strategy;

import com.example.elastic.models.enums.FileType;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Text extraction strategy for PDF files using Apache PDFBox.
 * Extracts text from both text-based and image-based PDFs.
 */
@Service
public class PdfTextExtractor implements TextExtractorStrategy {

    @Override
    public String extractText(byte[] fileBytes, String fileName) throws IOException {
        try (PDDocument document = Loader.loadPDF(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    @Override
    public boolean supports(FileType fileType) {
        return fileType == FileType.PDF;
    }
}
