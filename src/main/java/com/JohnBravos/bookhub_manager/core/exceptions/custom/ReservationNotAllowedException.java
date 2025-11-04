package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ReservationNotAllowedException extends BaseException {

    private final String reason;

    public ReservationNotAllowedException(String reason)
    {
        super(HttpStatus.BAD_REQUEST, "RESERVATION_NOT_ALLOWED",
                "Reservation is not allowerd " + reason);
        this.reason = reason;
    }

  public ReservationNotAllowedException(String reason, String suggestion) {
    super(HttpStatus.BAD_REQUEST, "RESERVATION_NOT_ALLOWED",
            String.format("Reservation not allowed: %s. Suggestion: %s", reason, suggestion));
    this.reason = reason;
  }

    public String getReason() {
      return reason;
    }
}
