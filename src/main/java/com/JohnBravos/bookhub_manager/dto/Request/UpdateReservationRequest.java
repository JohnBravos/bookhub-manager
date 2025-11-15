package com.JohnBravos.bookhub_manager.dto.Request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateReservationRequest(

        Long bookId,

        @NotNull(message = "Expiry date is required")
        @Future(message = "Expiry date must be in the future")
        LocalDate expiryDate

        // bookId, αλλαγή βιβλίου
        // expiryDate, παράταση κράτησης
) {}
