package com.JohnBravos.bookhub_manager.service;

import com.JohnBravos.bookhub_manager.core.enums.LoanStatus;
import com.JohnBravos.bookhub_manager.dto.Request.CreateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.ReturnLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Response.LoanResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ILoanService {

    // CREATE
    LoanResponse createLoan(CreateLoanRequest request);

    // HANDLE LOAN REQUESTS
    LoanResponse approveLoan(Long loanId);
    LoanResponse rejectLoan(Long loanId);

    // READ
    LoanResponse getLoanById(Long id);
    Page<LoanResponse> getAllLoans(int page, int size, String sort, String status);
    Page<LoanResponse> getLoansByUser(Long userId, int page, int size, String sort);
    List<LoanResponse> getLoansByBook(Long bookId);
    Page<LoanResponse> getLoansByStatus(LoanStatus status, int page, int size, String sort);
    Page<LoanResponse> getActiveLoans(int page, int size, String sort);
    List<LoanResponse> getOverdueLoans();
    List<LoanResponse> getLoansDueSoon();

    // UPDATE
    LoanResponse updateLoan(Long loanId, UpdateLoanRequest request);
    LoanResponse returnLoan(ReturnLoanRequest request);
    LoanResponse renewLoan(Long loanId);

    // DELETE
    void deleteLoan(Long loanId);

    // UTILITY
    boolean loanExists(Long loanId);
    boolean hasActiveLoan(Long userId, Long bookId);
    int getActiveLoansCountByUser(Long userId);
}
