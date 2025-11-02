package com.JohnBravos.bookhub_manager.dto.Request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateLoanRequest(
        @NotNull(message = "Due date is required")
        @Future(message = "Due date must be in the future")
        LocalDate dueDate

        // Στα Loans ενημερώνουμε συνήθως μόνο το dueDate
        // Το returnDate και Status το ορίζει το system όταν επιστρέφεται το βιβλίο
) {}
