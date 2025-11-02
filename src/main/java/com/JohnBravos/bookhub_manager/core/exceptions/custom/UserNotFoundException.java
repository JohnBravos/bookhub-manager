package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(Long userId)
    {
        super(HttpStatus.NOT_FOUND, "USER_NOT_FOUND",
                "User not found with ID: " + userId);
    }

    public UserNotFoundException(String email) {
        super(HttpStatus.NOT_FOUND, "USER_NOT_FOUND",
                "User not found with email: " + email);
    }

    // For logging and debugging
    public String getDetailedMessage() {
        return String.format("UserNotFoundException: %s (code: %s)", getMessage(), getErrorCode());
    }
}
