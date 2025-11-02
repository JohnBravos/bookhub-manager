package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class AuthorNotFoundException extends BaseException {

    public AuthorNotFoundException(Long authorId)
    {
        super(HttpStatus.NOT_FOUND, "AUTHOR_NOT_FOUND",
                "Author not found with ID: " + authorId);
    }

    public AuthorNotFoundException(String name) {
        super(HttpStatus.NOT_FOUND, "AUTHOR_NOT_FOUND",
                "Author not found with ID: " + name);
    }
}
