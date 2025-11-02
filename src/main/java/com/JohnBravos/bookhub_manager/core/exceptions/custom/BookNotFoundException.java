package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class BookNotFoundException extends BaseException
{
    public BookNotFoundException(Long bookId)
    {
        super(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND",
                "Book not found with ID: " + bookId);
    }
}
