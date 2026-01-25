package com.example.elastic.services.extractors;

import com.example.elastic.models.enums.FileType;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Service for detecting file types from byte arrays using Apache Tika.
 * Analyzes file content (magic bytes) and filename to determine MIME type.
 */
@Service
public class FileTypeDetector {

    private final Tika tika;

    public FileTypeDetector() {
        this.tika = new Tika();
    }

    /**
     * Detects the file type from byte array and filename.
     *
     * @param fileBytes the file content as bytes
     * @param fileName the original filename (optional, can be null)
     * @return the detected FileType enum value
     */
    public FileType detectFileType(byte[] fileBytes, String fileName) {
        try {
            String mimeType = tika.detect(new ByteArrayInputStream(fileBytes), fileName);
            return FileType.fromMimeType(mimeType);
        } catch (IOException e) {
            return detectFromExtension(fileName);
        }
    }

    /**
     * Fallback detection using file extension only.
     *
     * @param fileName the filename to analyze
     * @return the detected FileType based on extension
     */
    private FileType detectFromExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return FileType.UNSUPPORTED;
        }

        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        return switch (extension) {
            case "png" -> FileType.IMAGE_PNG;
            case "jpg", "jpeg" -> FileType.IMAGE_JPEG;
            case "tiff", "tif" -> FileType.IMAGE_TIFF;
            case "bmp" -> FileType.IMAGE_BMP;
            case "pdf" -> FileType.PDF;
            case "doc" -> FileType.DOC;
            case "docx" -> FileType.DOCX;
            case "xls" -> FileType.XLS;
            case "xlsx" -> FileType.XLSX;
            case "ppt" -> FileType.PPT;
            case "pptx" -> FileType.PPTX;
            case "txt" -> FileType.TXT;
            case "csv" -> FileType.CSV;
            case "json" -> FileType.JSON;
            case "xml" -> FileType.XML;
            default -> FileType.UNSUPPORTED;
        };
    }
}
