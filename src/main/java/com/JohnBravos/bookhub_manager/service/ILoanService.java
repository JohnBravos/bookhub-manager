package com.JohnBravos.bookhub_manager.service;

import com.JohnBravos.bookhub_manager.core.enums.LoanStatus;
import com.JohnBravos.bookhub_manager.dto.Request.CreateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.ReturnLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Response.LoanResponse;

import java.util.List;

public interface ILoanService {

    // CREATE
    LoanResponse createLoan(CreateLoanRequest request);

    // READ
    LoanResponse getLoanById(Long id);
    List<LoanResponse> getAllLoans();
    List<LoanResponse> getLoansByUser(Long userId);
    List<LoanResponse> getLoansByBook(Long bookId);
    List<LoanResponse> getLoansByStatus(LoanStatus status);
    List<LoanResponse> getActiveLoans();
    List<LoanResponse> getOverdueLoans();
    List<LoanResponse> getLoansDueSoon();

    // UPDATE
    LoanResponse updateLoan(Long loanId, UpdateLoanRequest request);
    LoanResponse returnLoan(ReturnLoanRequest request);

    // DELETE
    void deleteLoan(Long loanId);

    // UTILITY
    boolean loanExists(Long loanId);
    boolean hasActiveLoan(Long userId, Long bookId);
    int getActiveLoansCountByUser(Long userId);
}
