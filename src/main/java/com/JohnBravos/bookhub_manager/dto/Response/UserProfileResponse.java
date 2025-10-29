package com.JohnBravos.bookhub_manager.dto.Response;

import com.JohnBravos.bookhub_manager.model.Reservation;

import java.util.List;

public record UserProfileResponse(

        UserResponse user,
        int activeLoansCount,
        int totalLoansCount,
        List<LoanResponse> currentLoans,
        List<ReservationResponse> currentReservations,

        // Statistics
        int booksReadCount,
        String favoriteGenre,
        Double averageRating
) {}
