package com.example.elastic.services.processors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Handles file I/O operations.
 * Single Responsibility: Manage temporary file creation and cleanup
 */
public class FileHandlingService {

    /**
     * Create a temporary file from byte array
     * @param documentId Document ID for file naming
     * @param fileBytes The file contents as bytes
     * @return The created temporary file
     * @throws IOException if file creation fails
     */
    public static File createTempFileFromBytes(String documentId, byte[] fileBytes) throws IOException {
        File tempFile = File.createTempFile("upload-" + documentId, ".tmp");
        Files.write(tempFile.toPath(), fileBytes);
        return tempFile;
    }

    /**
     * Delete a file if it exists
     * @param file The file to delete
     */
    public static void deleteTempFile(File file) {
        try {
            if (file != null) {
                Files.deleteIfExists(file.toPath());
            }
        } catch (IOException e) {
            System.err.println("Failed to delete temp file: " + e.getMessage());
        }
    }
}
