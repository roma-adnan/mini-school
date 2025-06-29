package com.school.mini.exception;


public class ValidationException extends RuntimeException {
    private final String errorMessage;

    public ValidationException(String errorMessage) {
        super("Validation Error");
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
