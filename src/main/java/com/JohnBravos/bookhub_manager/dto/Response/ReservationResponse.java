package com.JohnBravos.bookhub_manager.dto.Response;

import com.JohnBravos.bookhub_manager.core.enums.ReservationStatus;

import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        BookResponse book,
        UserResponse user,
        LocalDate reservationDate,
        LocalDate expiryDate,
        ReservationStatus status,
        int positionInQueue
) {}
