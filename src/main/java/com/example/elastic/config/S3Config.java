package com.example.elastic.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${aws.s3.endpoint}")
    private String endpoint;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Getter
    @Value("${aws.s3.bucket}")
    private String bucket;

    private S3Client s3Client;

    @Bean
    public S3Client s3Client() {
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
        return s3Client;
    }

    /**
     *
     * This method will ensure the bucket exists before any read/write operation
     * It will first check whether the bucket is available or not, If the bucket is not available,
     * It will create one with specified name
     */
    @PostConstruct
    public void ensureBucketExists() {
        try (S3Client client = s3Client()) {
            HeadBucketRequest head = HeadBucketRequest.builder()
                    .bucket(bucket)
                    .build();
            client.headBucket(head);
        } catch (NoSuchBucketException e) {
            try (S3Client client = s3Client()) {
                client.createBucket(CreateBucketRequest.builder()
                        .bucket(bucket)
                        .createBucketConfiguration(
                                CreateBucketConfiguration.builder()
                                        .locationConstraint(region)
                                        .build()
                        )
                        .build());
                System.out.println("Bucket created: " + bucket);
            } catch (Exception ex) {
                System.err.println("Failed to create bucket: " + ex.getMessage());
            }
        } catch (Exception ex) {
            System.err.println("Bucket check/creation failed: " + ex.getMessage());
        }
    }

}
