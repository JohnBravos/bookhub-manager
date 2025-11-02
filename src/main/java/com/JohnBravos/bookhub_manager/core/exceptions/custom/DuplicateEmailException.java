package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends BaseException {
    public DuplicateEmailException(String email)
    {
        super(HttpStatus.NOT_FOUND, "DUPLICATE_EMAIL",
                "Email '" + email + "' is already registered");
    }

    // Business logic method
    public String getDetailedMessage() {
        return "Please use a different email address or try password reset";
    }
}
