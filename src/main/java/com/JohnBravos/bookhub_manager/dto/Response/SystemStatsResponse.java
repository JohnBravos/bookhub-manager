package com.JohnBravos.bookhub_manager.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatsResponse {
    private long totalUsers;
    private long totalBooks;
    private long availableBooks;
    private long activeLoans;
    private long totalLoans;
    private long overdueLoans;
    private long totalReservations;
    private long pendingReservations;
}
