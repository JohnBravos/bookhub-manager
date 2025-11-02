package com.JohnBravos.bookhub_manager.dto.Response;

import com.JohnBravos.bookhub_manager.model.Reservation;
import lombok.Builder;

import java.util.List;

@Builder
public record UserProfileResponse(

        UserResponse user,
        int activeLoansCount,
        int totalLoansCount,
        int totalReservationsCount,
        List<LoanResponse> currentLoans,
        List<ReservationResponse> currentReservations,

        // Statistics
        int booksReadCount,
        String favoriteGenre,
        Double averageRating
) {}
