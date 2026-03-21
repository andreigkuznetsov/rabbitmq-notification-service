package com.example.notificationservice.exception;

public class NonRetryableProviderException extends RuntimeException {

    public NonRetryableProviderException(String message) {
        super(message);
    }
}
