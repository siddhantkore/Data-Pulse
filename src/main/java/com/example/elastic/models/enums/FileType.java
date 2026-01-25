package com.example.elastic.models.enums;

/**
 * Enumeration of supported file types for document processing.
 * Each type specifies the extraction strategy to use.
 */
public enum FileType {
    /** Image files requiring OCR extraction. */
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_JPG("image/jpg"),
    IMAGE_TIFF("image/tiff"),
    IMAGE_BMP("image/bmp"),

    /** PDF documents. */
    PDF("application/pdf"),

    /** Microsoft Office documents. */
    DOC("application/msword"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    XLS("application/vnd.ms-excel"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PPT("application/vnd.ms-powerpoint"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),

    /** Plain text files. */
    TXT("text/plain"),
    CSV("text/csv"),
    JSON("application/json"),
    XML("text/xml"),

    /** Unsupported or unknown file types. */
    UNSUPPORTED("application/octet-stream");

    private final String mimeType;

    FileType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Gets the MIME type associated with this file type.
     *
     * @return the MIME type string
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Determines if this file type requires OCR extraction.
     *
     * @return true if OCR is needed, false otherwise
     */
    public boolean requiresOcr() {
        return this.name().startsWith("IMAGE_");
    }

    /**
     * Determines if this file type is an Office document.
     *
     * @return true if Office document, false otherwise
     */
    public boolean isOfficeDocument() {
        return this == DOC || this == DOCX || this == XLS 
            || this == XLSX || this == PPT || this == PPTX;
    }

    /**
     * Determines if this file type is plain text.
     *
     * @return true if plain text, false otherwise
     */
    public boolean isPlainText() {
        return this == TXT || this == CSV || this == JSON || this == XML;
    }

    /**
     * Finds a FileType by MIME type string.
     *
     * @param mimeType the MIME type to match
     * @return the matching FileType, or UNSUPPORTED if no match found
     */
    public static FileType fromMimeType(String mimeType) {
        if (mimeType == null) {
            return UNSUPPORTED;
        }
        
        for (FileType type : values()) {
            if (type.mimeType.equalsIgnoreCase(mimeType)) {
                return type;
            }
        }
        return UNSUPPORTED;
    }
}
