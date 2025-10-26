package com.example.elastic.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * The path to your Google Service Account JSON key file.
 * Authorizes and creates the Google Drive API client services.
 * For simplicity, this example uses a Service Account flow.
 */
@Configuration
public class GoogleDriveConfig {

    @Value("${spring.drive.path}")
    private String credentialsPath;

    private static final String APPLICATION_NAME = "elastic";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    @Bean
    public Drive driveService() throws IOException {
        List<String> scopes = Collections.singletonList(DriveScopes.DRIVE_FILE);

        InputStream in = GoogleDriveConfig.class.getResourceAsStream(credentialsPath);
        if (in == null) {
            java.io.File file = new java.io.File(credentialsPath);
            if (!file.exists()) {
                throw new IOException("Credentials file not found at path: " + credentialsPath);
            }
            in = new FileInputStream(file);
        }

        Credential credential = null;// GoogleCredential.fromStream(in)
//                .createScoped(scopes); // use correct .json as this is not serve specific refer google cloud console

        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
