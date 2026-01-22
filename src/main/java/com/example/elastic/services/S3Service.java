package com.example.elastic.services;

import com.example.elastic.config.S3Config;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
    // Export all the S3 config and functionality to this class from DocumentProcessorService

    private final S3Client s3Client;
    private final String bucket;

    @Autowired
    public S3Service(S3Client s3Client, S3Config s3Config) {
        this.s3Client = s3Client;
        this.bucket = s3Config.getBucket();
    }

    /**
     *
     * @param file the file to upload
     * @param key the S3 key for the file
     * @return the S3 key of the uploaded file
     * @throws IOException if upload fails
     */
    public String uploadFile(MultipartFile file, String key) throws IOException {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
        );
        return key;
    }

    /**
     *
     * @param key the S3 key of the file to download
     * @return the file content as byte array
     */
    public byte[] downloadFile(String key) {
        return s3Client.getObjectAsBytes(b -> b.bucket(bucket).key(key)).asByteArray();
    }

    /**
     *
     * @param key
     */
    public void deleteFile(String key) {
        s3Client.deleteObject(b -> b.bucket(bucket).key(key));
    }
}
