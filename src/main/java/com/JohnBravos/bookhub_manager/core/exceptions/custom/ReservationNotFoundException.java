package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ReservationNotFoundException extends BaseException
{
    public ReservationNotFoundException(Long reservationId)
    {
        super(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND",
                "Reservation not found with ID: " + reservationId);
    }

    public ReservationNotFoundException(Long userId, Long bookId) {
        super(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND",
                "Active reservation not found for user ID: " + userId + " and book ID: " + bookId);
    }
}
