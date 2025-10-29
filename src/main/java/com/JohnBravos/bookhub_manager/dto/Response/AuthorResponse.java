package com.JohnBravos.bookhub_manager.dto.Response;

import java.time.LocalDateTime;

public record AuthorResponse(
        Long id,
        String firstName,
        String lastName,
        String nationality,
        String fullName,
        String biography,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
