package com.example.elastic.service;

import com.example.elastic.model.DocumentMetadata;
import com.example.elastic.repository.DocumentMongoRepository;
import com.example.elastic.repository.DocumentSearchRepository;
import com.example.elastic.service.llm.LLMService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentMongoRepository documentMongoRepository;
    private final DocumentSearchRepository searchRepository;
    private final LLMService llmService;

    private final ObjectMapper mapper;
//    private final S3Client s3Client;
//    private final String bucket;
//    private final String region;

    public DocumentService(
            DocumentMongoRepository documentMongoRepository,
            DocumentSearchRepository searchRepository,
            LLMService llmService, ObjectMapper objectMapper
//            @Value("${s3.endpoint}") String endpoint,
//            @Value("${s3.access-key}") String accessKey,
//            @Value("${s3.secret-key}") String secretKey,
//            @Value("${s3.region}") String region,
//            @Value("${s3.bucket}") String bucket
    ) {
        this.documentMongoRepository = documentMongoRepository;
        this.searchRepository = searchRepository;
        this.llmService = llmService;
        this.mapper = objectMapper;
//        this.bucket = bucket;
//
//        this.s3Client = S3Client.builder()
//                .endpointOverride(java.net.URI.create(endpoint))
//                .region(Region.of(region))
//                .credentialsProvider(StaticCredentialsProvider.create(
//                        AwsBasicCredentials.create(accessKey, secretKey)))
//                .serviceConfiguration(S3Configuration.builder()
//                        .pathStyleAccessEnabled(true)  // 👈 important for MinIO in Docker
//                        .build())
//                .build();
//        this.region = region;

    }

//    @PostConstruct
//    public void initBucket() {
//        try {
//            // Check if bucket exists
//            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
//                    .bucket(bucket)
//                    .build();
//            s3Client.headBucket(headBucketRequest);
//        } catch (NoSuchBucketException e) {
//            // Create if not exists
//            s3Client.createBucket(CreateBucketRequest.builder()
//                    .bucket(bucket)
//                    .createBucketConfiguration(
//                            CreateBucketConfiguration.builder()
//                                    .locationConstraint(region)
//                                    .build()
//                    )
//                    .build());
//        } catch (Exception ex) {
//            // Log but don't crash the app
//            System.err.println("Bucket check/creation failed: " + ex.getMessage());
//        }
//    }


    public DocumentMetadata processAndStore(MultipartFile file) {
        DocumentMetadata documentMetadata = new DocumentMetadata();
        try {
            String key = UUID.randomUUID() + "-" + file.getOriginalFilename();

            // 1. Upload file to MinIO
//        s3Client.putObject(PutObjectRequest.builder()
//                        .bucket(bucket)
//                        .key(key)
//                        .build(),
//                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

            // 2. Save file temporarily for OCR
            File tempFile = File.createTempFile("upload", file.getOriginalFilename());
            file.transferTo(tempFile);

            // 3. Extract text using Tesseract
            System.out.println("1");
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
            tesseract.setLanguage("eng");
            tesseract.setOcrEngineMode(1); // LSTM
            tesseract.setPageSegMode(6);
            String extractedText = tesseract.doOCR(tempFile);
            System.out.println(extractedText);


            // 4. Store metadata in MongoDB
            DocumentMetadata metadata = new DocumentMetadata();
            metadata.setFileName(file.getOriginalFilename());
            metadata.setS3Key(key);
            String cleaned = extractedText
                    .replaceAll("[\\n\\r]+", "\n")   // normalize line breaks
                    .replaceAll("\\s{2,}", " ")      // collapse extra spaces
                    .trim();

            String llmResponse = llmService.processWithOpenAPI(cleaned)
                    .trim()
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

//        cleanUp to follow sanitization
/*
        private String sanitizeLLMResponse(String llmResponse) {
            if (llmResponse == null) return "{}";
            return llmResponse
                    .trim()
                    .replaceAll("(?s)```json", "")
                    .replaceAll("(?s)```", "")
                    .replaceAll("^[^\\{]*", "")  // remove everything before first {
                    .replaceAll("[^\\}]*$", ""); // remove everything after last }
        }
*/

//        metadata.setExtractedText(llmResponse);
// Instead of doing this we should map the got response to our entity class for better field utilization
            System.out.println(metadata);

            documentMetadata = mapLLMResponseToModelClass(llmResponse, metadata);

//        documentMongoRepository.save(metadata);
            documentMongoRepository.save(documentMetadata);


            // 5. Index in Elasticsearch
            System.out.println("Going to save in Elastic");
            searchRepository.save(metadata);

            // Cleanup
            Files.deleteIfExists(tempFile.toPath());
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return documentMetadata;
    }

    private DocumentMetadata mapLLMResponseToModelClass(String llmResponse, DocumentMetadata metadata) throws JsonProcessingException {

        JsonNode root = mapper.readTree(llmResponse);

        metadata.setCategories(
                mapper.convertValue(root.get("categories"), new TypeReference<List<String>>() {})
        );
        metadata.setKeywords(
                mapper.convertValue(root.get("keywords"), new TypeReference<List<String>>() {})
        );

// metadata object
        DocumentMetadata.Metadata meta = new DocumentMetadata.Metadata();
        meta.setTitle(root.path("metadata").path("title").asText());
        meta.setAuthorOrSender(root.path("metadata").path("author_or_sender").asText());
        meta.setDate(root.path("metadata").path("date").asText());
        meta.setEntities(
                mapper.convertValue(root.path("metadata").path("entities"), new TypeReference<List<String>>() {})
        );
        meta.setSummary(root.path("metadata").path("summary").asText());

        metadata.setMetadata(meta);

// flexible fields
        metadata.setDocumentSpecificFields(
                mapper.convertValue(root.get("document_specific_fields"), new TypeReference<Map<String, Object>>() {})
        );

        return metadata;
    }
}
