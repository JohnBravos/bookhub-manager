package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends BaseException {
    public AccessDeniedException(String message)
    {
        super(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "You can only access your own loans");
    }
}
