package com.JohnBravos.bookhub_manager.dto.Response;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        LocalDateTime timestamp,
        String path
) {

    // ========== SUCCESS METHODS ==========

    /**
     * Success response with data only
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, LocalDateTime.now(), null);
    }

    /**
     * Success response with data and message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), null);
    }

    /**
     * Success response with message only (for operations that don't return data)
     */
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, message, null, LocalDateTime.now(), null);
    }

    // ========== ERROR METHODS ==========

    /**
     * Error response with message
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), null);
    }

    /**
     * Error response with message and path
     */
    public static <T> ApiResponse<T> error(String message, String path) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), path);
    }

    // ========== BUILDER-STYLE METHODS ==========

    /**
     * Add path to response (for better debugging)
     */
    public ApiResponse<T> withPath(String path) {
        return new ApiResponse<>(this.success, this.message, this.data, this.timestamp, path);
    }

    /**
     * Add timestamp to response
     */
    public ApiResponse<T> withTimestamp(LocalDateTime timestamp) {
        return new ApiResponse<>(this.success, this.message, this.data, timestamp, this.path);
    }
}
