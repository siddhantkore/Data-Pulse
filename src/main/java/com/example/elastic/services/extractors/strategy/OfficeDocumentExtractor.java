package com.example.elastic.services.extractors.strategy;

import com.example.elastic.models.enums.FileType;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Text extraction strategy for Microsoft Office documents using Apache POI.
 * Supports Word (DOC/DOCX), Excel (XLS/XLSX), and PowerPoint (PPT/PPTX).
 */
@Service
public class OfficeDocumentExtractor implements TextExtractorStrategy {

    @Override
    public String extractText(byte[] fileBytes, String fileName) throws IOException {
        FileType fileType = FileType.fromMimeType(detectMimeType(fileName));
        
        return switch (fileType) {
            case DOC -> extractFromDoc(fileBytes);
            case DOCX -> extractFromDocx(fileBytes);
            case XLS -> extractFromXls(fileBytes);
            case XLSX -> extractFromXlsx(fileBytes);
            case PPT -> extractFromPpt(fileBytes);
            case PPTX -> extractFromPptx(fileBytes);
            default -> throw new IOException("Unsupported Office document type");
        };
    }

    @Override
    public boolean supports(FileType fileType) {
        return fileType.isOfficeDocument();
    }

    /**
     * Extracts text from legacy Word documents (.doc).
     */
    private String extractFromDoc(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
             HWPFDocument document = new HWPFDocument(bis);
             WordExtractor extractor = new WordExtractor(document)) {
            return extractor.getText();
        }
    }

    /**
     * Extracts text from modern Word documents (.docx).
     */
    private String extractFromDocx(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
             XWPFDocument document = new XWPFDocument(bis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    /**
     * Extracts text from legacy Excel spreadsheets (.xls).
     */
    private String extractFromXls(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
             Workbook workbook = new HSSFWorkbook(bis)) {
            return extractFromWorkbook(workbook);
        }
    }

    /**
     * Extracts text from modern Excel spreadsheets (.xlsx).
     */
    private String extractFromXlsx(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
             Workbook workbook = new XSSFWorkbook(bis)) {
            return extractFromWorkbook(workbook);
        }
    }

    /**
     * Extracts text from PowerPoint presentations (.ppt).
     */
    private String extractFromPpt(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
             HSLFSlideShow slideShow = new HSLFSlideShow(bis)) {
            StringBuilder text = new StringBuilder();
            slideShow.getSlides().forEach(slide -> 
                text.append(slide.getTitle()).append("\n")
            );
            return text.toString();
        }
    }

    /**
     * Extracts text from modern PowerPoint presentations (.pptx).
     */
    private String extractFromPptx(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
             XMLSlideShow slideShow = new XMLSlideShow(bis)) {
            StringBuilder text = new StringBuilder();
            slideShow.getSlides().forEach(slide -> {
                slide.getShapes().forEach(shape -> 
                    text.append(shape.getShapeName()).append("\n")
                );
            });
            return text.toString();
        }
    }

    /**
     * Helper method to extract text from Excel workbook.
     */
    private String extractFromWorkbook(Workbook workbook) {
        StringBuilder text = new StringBuilder();
        workbook.forEach(sheet -> {
            text.append("Sheet: ").append(sheet.getSheetName()).append("\n");
            sheet.forEach(row -> 
                row.forEach(cell -> 
                    text.append(cell.toString()).append("\t")
                )
            );
            text.append("\n");
        });
        return text.toString();
    }

    /**
     * Simple MIME type detection from filename extension.
     */
    private String detectMimeType(String fileName) {
        if (fileName == null) {
            return "";
        }
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default -> "";
        };
    }
}
