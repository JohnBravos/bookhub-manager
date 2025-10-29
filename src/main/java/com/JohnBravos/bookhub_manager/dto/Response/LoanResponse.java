package com.JohnBravos.bookhub_manager.dto.Response;

import com.JohnBravos.bookhub_manager.core.enums.LoanStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanResponse(
        Long id,
        BookResponse book,
        UserResponse user,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDateTime returnDate,
        LoanStatus status,
        boolean isOverdue,
        int daysOverdue
) {}
