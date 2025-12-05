package com.JohnBravos.bookhub_manager.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingsRequest {
    @NotNull(message = "Max loans per member is required")
    @Min(value = 1, message = "Max loans per member must be at least 1")
    private int maxLoansPerMember;
    
    @NotNull(message = "Loan period days is required")
    @Min(value = 1, message = "Loan period must be at least 1 day")
    private int loanPeriodDays;
    
    @NotNull(message = "Max reservations per book is required")
    @Min(value = 1, message = "Max reservations must be at least 1")
    private int maxReservationsPerBook;
    
    @NotNull(message = "Late fee per day is required")
    @Min(value = 0, message = "Late fee cannot be negative")
    private double lateFeePerDay;
    
    private boolean renewalAllowed;
    
    @Min(value = 0, message = "Max renewals cannot be negative")
    private int maxRenewals;
}
