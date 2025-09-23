package com.example.elastic.controller;

import com.example.elastic.connectors.mail_connectors.GmailIMAPConnector;
import com.example.elastic.model.DocumentMetadata;
import com.example.elastic.service.DocumentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;
    private final GmailIMAPConnector connector;

    public DocumentController(DocumentService documentService, GmailIMAPConnector connector) {
        this.documentService = documentService;
        this.connector = connector;
    }

    /**
     * @route /api/documents/upload
     * @param file take Multipart file as input
     * @return model DocumentMetadata after successfully processing & saving it
     * Calls processAndStore in DocumentService
     */
    @PostMapping("/upload")
    public ResponseEntity<DocumentMetadata> uploadFile(@RequestParam("file") MultipartFile file) {
        DocumentMetadata saved = documentService.processAndStore(file);
        return ResponseEntity.ok(saved);
    }

    /**
     * @return currently row implementation
     */
    @GetMapping("/pull")
    public ResponseEntity<String> pullMails () {
        if(connector.sendToDocumentService()){
            return ResponseEntity.ok("Ok");
        }
        return ResponseEntity.ok("Bad Not OK");
    }

    @GetMapping("/get")
    public ResponseEntity<File> getDocument() {
        return null;
    }
}
