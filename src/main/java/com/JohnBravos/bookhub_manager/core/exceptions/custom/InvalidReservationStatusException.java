package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidReservationStatusException extends BaseException {
    public InvalidReservationStatusException(String status) {
        super(HttpStatus.NOT_FOUND, "INVALID_RESERVATION_STATUS",
                "The reservation status '" + status + "' is invalid.");
    }
}
