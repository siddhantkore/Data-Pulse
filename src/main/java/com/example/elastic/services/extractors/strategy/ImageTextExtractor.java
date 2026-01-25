package com.example.elastic.services.extractors.strategy;

import com.example.elastic.models.enums.FileType;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Text extraction strategy for image files using Tesseract OCR.
 * Supports PNG, JPEG, TIFF, BMP and other image formats.
 */
@Service
public class ImageTextExtractor implements TextExtractorStrategy {

    private final String tesseractDataPath;

    public ImageTextExtractor(@Value("${tesseract.datapath}") String tesseractDataPath) {
        this.tesseractDataPath = tesseractDataPath;
    }

    @Override
    public String extractText(byte[] fileBytes, String fileName) throws IOException, TesseractException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
        
        if (image == null) {
            throw new IOException("Unable to read image from bytes");
        }

        Tesseract tesseract = initializeTesseract();
        return tesseract.doOCR(image);
    }

    @Override
    public boolean supports(FileType fileType) {
        return fileType.requiresOcr();
    }

    /**
     * Initializes and configures Tesseract instance.
     *
     * @return configured Tesseract instance
     */
    private Tesseract initializeTesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tesseractDataPath);
        tesseract.setLanguage("eng");
        tesseract.setOcrEngineMode(1);
        tesseract.setPageSegMode(6);
        return tesseract;
    }
}
