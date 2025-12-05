package com.JohnBravos.bookhub_manager.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingsResponse {
    private int maxLoansPerMember;
    private int loanPeriodDays;
    private int maxReservationsPerBook;
    private double lateFeePerDay;
    private boolean renewalAllowed;
    private int maxRenewals;
}
