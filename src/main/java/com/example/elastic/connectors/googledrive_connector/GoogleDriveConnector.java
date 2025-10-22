package com.example.elastic.connectors.googledrive_connector;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDriveConnector {
    private static final String APPLICATION_NAME = "elastic";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    // The Drive service instance used for all API calls
    private final Drive driveService;

    /**
     * Initializes the DriveDocumentPuller by creating the authorized Drive service.
     * @param credentialsPath The path to your Google Service Account JSON key file.
     * @throws IOException If the credentials file cannot be loaded or authentication fails.
     */
    public GoogleDriveConnector(String credentialsPath) throws IOException {
        this.driveService = getDriveService(credentialsPath);
        System.out.println("Google Drive Service initialized successfully.");
    }

    /**
     * Authorizes and creates the Google Drive API client service.
     * For simplicity, this example uses a Service Account flow.
     *
     * @param credentialsPath The path to the Service Account JSON key file.
     * @return An authorized Drive service instance.
     * @throws IOException
     */
    private Drive getDriveService(String credentialsPath) throws IOException {
        // Define the scope: read-only access to files, for secure file pulling.
        List<String> scopes = Collections.singletonList(DriveScopes.DRIVE_FILE);

        // Load credentials from the service account JSON key file
        InputStream in = GoogleDriveConnector.class.getResourceAsStream(credentialsPath);
        if (in == null) {
            // Fallback for direct file path if running outside a JAR/classpath
            java.io.File file = new java.io.File(credentialsPath);
            if (!file.exists()) {
                throw new IOException("Credentials file not found at path: " + credentialsPath);
            }
            in = new java.io.FileInputStream(file);
        }

        Credential credential = GoogleCredential.fromStream(in)
                .createScoped(scopes);

        // Build the Drive service
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Lists documents (e.g., PDFs) in Google Drive matching a specific query.
     *
     * @param query The Drive API search query string (e.g., "mimeType='application/pdf'").
     * @return A list of Google Drive File objects.
     * @throws IOException If the API call fails.
     */
    public List<File> listDocuments(String query) throws IOException {
        System.out.println("Searching Google Drive with query: '" + query + "'");

        // Use the Drive API's list method
        FileList result = driveService.files().list()
                .setQ(query)
                // Fields limits the response data, speeding up the call.
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
     * Downloads a file from Google Drive by its ID and saves it locally.
     * This method is the core "pull" functionality.
     *
     * @param fileId The ID of the file to download.
     * @param destinationPath The local path where the file should be saved.
     * @return true if the download was successful, false otherwise.
     */
    public boolean downloadFile(String fileId, String destinationPath) {
        System.out.printf("Attempting to download file ID %s to %s...\n", fileId, destinationPath);
        try (OutputStream outputStream = new FileOutputStream(destinationPath)) {

            // The 'get' request retrieves the file content stream.
            driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);

            System.out.println("Download successful! File saved to: " + destinationPath);
            return true;
        } catch (IOException e) {
            System.err.println("An error occurred during download: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        // --- START CONFIGURATION ---
        // REPLACE THIS WITH THE ACTUAL PATH TO YOUR SERVICE ACCOUNT JSON KEY FILE.
        // For local testing, ensure your key file is accessible.
        final String CREDENTIALS_FILE_PATH = "path/to/your/service-account-key.json";
        final String DOWNLOAD_FOLDER = "./downloads/"; // Create this folder for output

        // This query finds all non-trash PDF files.
        final String PDF_QUERY = "mimeType='application/pdf' and trashed=false";
        // --- END CONFIGURATION ---

        try {
            // 1. Initialize the puller service
            GoogleDriveConnector puller = new GoogleDriveConnector(CREDENTIALS_FILE_PATH);

            // Ensure the download directory exists
            new java.io.File(DOWNLOAD_FOLDER).mkdirs();

            // 2. List the target documents
            List<File> pdfFiles = puller.listDocuments(PDF_QUERY);

            // 3. Download the first document found (the "pull")
            if (!pdfFiles.isEmpty()) {
                File targetFile = pdfFiles.get(0);
                String localDestination = DOWNLOAD_FOLDER + targetFile.getName();
                puller.downloadFile(targetFile.getId(), localDestination);
            } else {
                System.out.println("Cannot download: No files were found to process.");
            }

        } catch (IOException e) {
            System.err.println("\n!!! FATAL ERROR !!!\nCould not initialize Drive Service. Check your credentials file path and permissions.");
            e.printStackTrace();
        }
    }
}
