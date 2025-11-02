package com.JohnBravos.bookhub_manager.core.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final String errorCode;
    private final String message;
    private final int statusCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private List<FieldError> fieldErrors;
    private String path;

    public ApiError(HttpStatus status, String message, String errorCode, LocalDateTime timestamp) {
        this.statusCode = status.value();
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
    }

    @Data
    public static class FieldError {
        private final String field;
        private final String message;
        private final Object rejectedValue;

        public FieldError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }
    }
}
