package com.JohnBravos.bookhub_manager.dto.Response;

import com.JohnBravos.bookhub_manager.core.enums.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        BookResponse book,
        UserResponse user,
        LocalDateTime reservationDate,
        LocalDateTime expiryDate,
        ReservationStatus status,
        int positionInQueue
) {}
