package com.example.elastic.connectors.googledrive_connector;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDriveConnector {

    // The Drive services instance used for all API calls
    private final Drive driveService;

    /**
     *
     * Initializes the DriveDocumentPuller by creating the authorized Drive services.
     */
    public GoogleDriveConnector(Drive driveService) {
        this.driveService = driveService;
    }

    /**
     *
     * @param query
     * @return
     * @throws IOException
     */
    public List<File> listDocuments(String query) throws IOException {
        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("nextPageToken, files(id, name, mimeType, modifiedTime, size)")
                .execute();

        List<File> files = result.getFiles();

        if (files == null || files.isEmpty()) {
            System.out.println("No files found matching the criteria.");
            return Collections.emptyList();
        }

        System.out.println("Found " + files.size() + " documents:");
        for (File file : files) {
            System.out.printf("- %s (%s, ID: %s)\n", file.getName(), file.getMimeType(), file.getId());
        }

        return files;
    }

    /**
     *
     * @param fileId
     * @param destinationPath
     * @return
     */
    public boolean downloadFile(String fileId, String destinationPath) {
        try (OutputStream outputStream = new FileOutputStream(destinationPath)) {
            driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
            System.out.println("Download successful ! File saved to: " + destinationPath);
            return true;
        } catch (IOException e) {
            System.err.println("An error occurred during download: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}