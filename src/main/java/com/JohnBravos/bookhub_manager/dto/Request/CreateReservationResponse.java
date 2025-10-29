package com.JohnBravos.bookhub_manager.dto.Request;

import jakarta.validation.constraints.NotNull;

public record CreateReservationResponse(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Book ID is required")
        Long bookId
) {}
