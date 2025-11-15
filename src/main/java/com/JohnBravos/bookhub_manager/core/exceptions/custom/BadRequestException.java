package com.JohnBravos.bookhub_manager.core.exceptions.custom;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
