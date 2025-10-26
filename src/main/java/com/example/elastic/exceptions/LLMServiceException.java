package com.example.elastic.exceptions;

public class LLMServiceException extends RuntimeException {
    public LLMServiceException(String message) {
        super(message);
    }
}
