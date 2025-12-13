package com.JohnBravos.bookhub_manager.dto.Response;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String path;

    public ApiResponse(boolean success, String message, T data, LocalDateTime timestamp, String path) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.path = path;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // ========== SUCCESS METHODS ==========
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), null);
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, message, null, LocalDateTime.now(), null);
    }

    // ========== ERROR METHODS ==========
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> error(String message, String path) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), path);
    }

    // ========== BUILDER-STYLE METHODS ==========
    public ApiResponse<T> withPath(String path) {
        return new ApiResponse<>(this.success, this.message, this.data, this.timestamp, path);
    }

    public ApiResponse<T> withTimestamp(LocalDateTime timestamp) {
        return new ApiResponse<>(this.success, this.message, this.data, timestamp, this.path);
    }
}
