package com.JohnBravos.bookhub_manager.dto.Response;

import lombok.Builder;

@Builder
public record UserStatisticsResponse(
        Long activeLoans,
        Long totalBorrowed,
        Long totalReservations,
        Long overdueCount
) {}
