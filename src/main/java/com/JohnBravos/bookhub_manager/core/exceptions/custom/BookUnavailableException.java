package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class BookUnavailableException extends BaseException {

    private final int availableCopies;
    private final int totalCopies;

    public BookUnavailableException(String bookTitle, int availableCopies, int totalCopies)
    {
        super(HttpStatus.BAD_REQUEST, "BOOK_UNAVAILABLE",
                String.format("Book '%s' is not available. Available copies: %d/%d",
                        bookTitle, availableCopies, totalCopies));
        this.availableCopies = availableCopies;
        this.totalCopies = totalCopies;
    }

    public boolean hasReservationOption() {
        return availableCopies == 0 && totalCopies > 0;
    }

    public String getReservationSuggestion() {
        return hasReservationOption() ?
                "This book can be reserved. Would you like to join the waiting list?" :
                "No copies available for reservation";
    }
}
