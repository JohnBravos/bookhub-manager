package com.JohnBravos.bookhub_manager.dto.Response;

import java.util.List;

public record BookDetailsResponse(
        BookResponse book,

        // Additional details for book page
        int activeLoansCount,
        int pendingReservationsCount,
        boolean isAvailable,
        int estimatedWaitDays,

        // Statistics
        int totalTimesBorrowed,
        double averageLoanDuration,

        // Related books
        List<BookResponse> sameAuthorBooks,
        List<BookResponse> similarGenreBooks
) {}
