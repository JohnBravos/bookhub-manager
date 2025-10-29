package com.JohnBravos.bookhub_manager.dto.Request;

import jakarta.validation.constraints.NotNull;

public record CancelReservationRequest(
        @NotNull(message = "Reservation ID is required")
        Long reservationId,

        String reason // optional
) {}
