package com.JohnBravos.bookhub_manager.dto.Response;

public record RegisterResponse(
        Long userId,
        String username,
        String email,
        String message
) {}
