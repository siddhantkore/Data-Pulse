package com.example.elastic.services.processors;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Handles OCR (Optical Character Recognition) text extraction from documents.
 * Single Responsibility: Extract text from images/PDFs using Tesseract
 */
@Service
public class OcrExtractionService {

    private final String tesseractDataPath;

    public OcrExtractionService(@Value("${tesseract.datapath}") String tesseractDataPath) {
        this.tesseractDataPath = tesseractDataPath;
    }

    /**
     * Extract text from a file using Tesseract OCR
     * @param tempFile The file to extract text from
     * @return Raw extracted text
     * @throws TesseractException if OCR fails
     */
    public String extractText(File tempFile) throws TesseractException {
        Tesseract tesseract = initializeTesseract();
        return tesseract.doOCR(tempFile);
    }

    /**
     * Initialize and configure Tesseract instance
     */
    private Tesseract initializeTesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tesseractDataPath);
        tesseract.setLanguage("eng");
        tesseract.setOcrEngineMode(1); // LSTM
        tesseract.setPageSegMode(6);
        return tesseract;
    }
}
