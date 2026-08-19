package com.kristoerte.inventoryservice.exception;

import org.springframework.http.HttpStatus;

public class GlobalException extends RuntimeException {

    private final HttpStatus status;

    public GlobalException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public GlobalException(String resourceType, String id, HttpStatus status) {
        super(resourceType + " not found: " + id);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
