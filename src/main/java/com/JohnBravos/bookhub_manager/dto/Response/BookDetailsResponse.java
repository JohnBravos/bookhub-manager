package com.JohnBravos.bookhub_manager.dto.Response;

import lombok.Builder;

import java.util.List;

@Builder
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
