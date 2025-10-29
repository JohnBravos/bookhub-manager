package com.JohnBravos.bookhub_manager.dto.Request;

import jakarta.validation.constraints.NotNull;

public record ReturnLoanRequest(

        @NotNull(message = "Loan ID is required")
        Long loanId,

        String notes // optional
) {}
