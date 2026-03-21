package com.example.notificationservice.exception;

public class RetryableProviderException extends RuntimeException {

    public RetryableProviderException(String message) {
        super(message);
    }
}
