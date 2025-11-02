package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateIsbnException extends BaseException {
    public DuplicateIsbnException(String isbn)
    {
        super(HttpStatus.CONFLICT, "DUPLICATE_ISBN",
                "ISBN '" + isbn + "' is already registered in the system");
    }

    public String getSuggestedSolution() {
        return "Please verify the ISBN or contact administrator if this is a " +
                "different book";
    }
}
