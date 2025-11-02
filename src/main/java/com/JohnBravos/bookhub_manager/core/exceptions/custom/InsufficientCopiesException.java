package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InsufficientCopiesException extends BaseException {

  private final int requested;
  private final int available;

  public InsufficientCopiesException(int requested, int available)
  {
    super(HttpStatus.BAD_REQUEST, "INSUFFICIENT_COPIES",
            String.format("Insufficient copies available. Requested: " +
                    "%d, Available: %d", requested, available));
        this.requested = requested;
        this.available = available;
  }

  public boolean canPartiallyFulfill() {
    return available > 0;
  }

  public int getMaxAvailable() {
    return available;
  }

  public String getSuggestion() {
    return canPartiallyFulfill() ?
            String.format("You can borrow up to %d copies", available) :
            "No copies available. Consider making a reservation";
  }
}
