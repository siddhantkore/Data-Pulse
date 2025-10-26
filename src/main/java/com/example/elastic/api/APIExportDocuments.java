package com.example.elastic.api;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pulse/api/req/docs")
@CrossOrigin("*")
public class APIExportDocuments {

    // implement a key based secure api gateway to access to the docs

    // Generate an API key for each client - each session (fixed period)
    // e.g. an api key is valid for 6 months only
    // save the generated api key with its other details for validation
    // for security consider using JWT
}
