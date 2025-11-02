package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class LoanNotFoundException extends BaseException {

    public LoanNotFoundException(Long loanId )
    {
        super(HttpStatus.NOT_FOUND, "LOAN_NOT_FOUND",
                "Loan not found with ID: " + loanId);
    }

    public LoanNotFoundException(Long userId, Long bookId) {
      super(HttpStatus.NOT_FOUND, "LOAN_NOT_FOUND",
              "Active loan not found for user ID: " + userId + " and book ID: " + bookId);
    }
}
