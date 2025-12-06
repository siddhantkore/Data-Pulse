package com.example.elastic.controllers;

import com.example.elastic.connectors.mail_connectors.GmailIMAPConnector;
import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.services.DocumentProcessorService;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentProcessorService documentProcessorService;
    private final GmailIMAPConnector connector;
//    private final S3DocumentFetchService s3Service;

    public DocumentController(DocumentProcessorService documentProcessorService, GmailIMAPConnector connector /*, S3DocumentFetchService s3Service*/) {
        this.documentProcessorService = documentProcessorService;
        this.connector = connector;
//        this.s3Service = s3Service;
    }

    /**
     * @param file take Multipart file as input
     * @return models DocumentMetadata after successfully processing and saving it
     * Calls processAndStore in DocumentService
     */
    @PostMapping("/upload")
    @CrossOrigin(origins="*")
    public ResponseEntity<DocumentMetadata> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        DocumentMetadata saved = documentProcessorService.processAndStore(file);
        return ResponseEntity.ok(saved);
    }

    /**
     * @return currently row implementation for email fetching
     */
    @GetMapping("/pull")
    public ResponseEntity<String> pullMails () {
        if(connector.sendToDocumentService()){
            return ResponseEntity.ok("Ok");
        }
        return ResponseEntity.ok("Bad Not OK");
    }


    /**
     *
     * @param s3Key and
     * @return The respective document from S3
     */
    @GetMapping("/download/{s3Key}")
    public ResponseEntity<byte[]> getDocument(@PathVariable String s3Key) {
//        byte[] content = s3Service.getDocument("my-bucket-name", s3Key);

//        return ResponseEntity.ok()
//                .header("Content-Type", "application/octet-stream")
//                .header("Content-Disposition", "attachment; filename=\"" + s3Key + "\"")
//                .body(content);
        return null;
    }
}
