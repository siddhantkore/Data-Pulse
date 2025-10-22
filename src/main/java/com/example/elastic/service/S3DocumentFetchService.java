package com.example.elastic.service;

import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.springframework.stereotype.Service;


@Service
public class S3DocumentFetchService {

    private final MinioClient minioClient;

    public S3DocumentFetchService() {
        this.minioClient = MinioClient.builder()
                        .endpoint("https://play.min.io")
                        .credentials("","")
                        .build();
        /*/ S3
        this.s3Client = S3Client.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build(); */
    }

    /**
     *
     * @param bucketName S3 bucket from which we want the Doc
     * @param key Object key of the document - s3 key from mongodb
     * @return Array of bytes a file
     * @exception NullPointerException is raised Exception
     */
    public byte[] getDocument(String bucketName, String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest)) {
                return response.readAllBytes();
            }
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
        return null;
    }

    /* Download to local file
    private Path downloadDocument(String bucketName, String key, String localFilePath) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        Path path = Path.of(localFilePath);
        s3Client.getObject(getObjectRequest, path);
        return path;
    }*/
}
