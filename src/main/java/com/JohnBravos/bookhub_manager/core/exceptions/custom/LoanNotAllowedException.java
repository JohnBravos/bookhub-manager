package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class LoanNotAllowedException extends BaseException {

    private final String reason;

    public LoanNotAllowedException(String reason)
    {
        super(HttpStatus.BAD_REQUEST, "LOAN_NOT_ALLOWED",
                "Loan operation not allowed: " + reason);
        this.reason = reason;
    }

    public LoanNotAllowedException(String reason, String suggestion) {
        super(HttpStatus.BAD_REQUEST, "LOAN_NOT_ALLOWED",
                String.format("Loan not allowed: %s. Suggestion: %s", reason, suggestion));
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
