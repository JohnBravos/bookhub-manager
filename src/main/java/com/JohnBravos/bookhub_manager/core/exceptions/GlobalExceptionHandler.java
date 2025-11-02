package com.JohnBravos.bookhub_manager.core.exceptions;

import com.JohnBravos.bookhub_manager.core.exceptions.custom.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Not found exceptions

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        log.warn("User not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiError> handleBookNotFound(BookNotFoundException ex, WebRequest request) {
        log.warn("Book not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ApiError> handleAuthorNotFound(AuthorNotFoundException ex, WebRequest request) {
        log.warn("Author not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ApiError> handleLoanNotFound(LoanNotFoundException ex, WebRequest request) {
        log.warn("Loan not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ApiError> handleReservationNotFound(ReservationNotFoundException ex, WebRequest request) {
        log.warn("Reservation not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // Conflict Exceptions

    @ExceptionHandler({DuplicateEmailException.class, DuplicateUsernameException.class})
    public ResponseEntity<ApiError> handleDuplicateExceptions(BaseException ex, WebRequest request) {
        log.warn("Duplicate data exception : {} - {}", ex.getErrorCode(), ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    // Business rule exceptions

    @ExceptionHandler({BookUnavailableException.class, InsufficientCopiesException.class, LoanNotAllowedException.class})
    public ResponseEntity<ApiError> handleBusinessRuleExceptions(BaseException ex, WebRequest request) {
        log.warn("Business rule violation: {} - {}", ex.getErrorCode(), ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(CannotDeleteException.class)
    public ResponseEntity<ApiError> handleCannotDelete(CannotDeleteException ex, WebRequest request) {
        log.warn("Delete operation not allowed: {} - {}", ex.getErrorCode(), ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    // Validation errors

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation errors: {}", ex.getMessage());

        List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiError.FieldError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more fields",
                "VALIDATION_ERROR",
                LocalDateTime.now()
        );
        apiError.setFieldErrors(fieldErrors);

        log.debug("Field errors: {}", fieldErrors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // Global exception handler

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.error("Internal server error: {}", ex.getMessage(), ex);

        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper method

    private ResponseEntity<ApiError> buildErrorResponse(BaseException ex, HttpStatus status,
                                                        WebRequest request) {
        ApiError apiError = new ApiError(
                status,
                ex.getMessage(),
                ex.getErrorCode(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiError, status);
    }
}
