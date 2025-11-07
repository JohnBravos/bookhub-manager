package com.JohnBravos.bookhub_manager.dto.Response;

public record LoginResponse(
   String token,
   Long userId,
   String username,
   String email,
   String role,
   String message
) {}
