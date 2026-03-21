package com.example.notificationservice.dto;

import java.util.Map;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private Map<String, String> fields;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String error, String message, Map<String, String> fields) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.fields = fields;
    }

}
