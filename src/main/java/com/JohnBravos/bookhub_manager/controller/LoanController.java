package com.JohnBravos.bookhub_manager.controller;

import com.JohnBravos.bookhub_manager.dto.Request.CreateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.ReturnLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ApiResponse;
import com.JohnBravos.bookhub_manager.dto.Response.LoanResponse;
import com.JohnBravos.bookhub_manager.service.ILoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final ILoanService loanService;

    // GET ALL LOANS (Librarian/Admin only)
    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getAllLoans() {
        log.info("Fetching all loans");
        List<LoanResponse> loans = loanService.getAllLoans();
        return ResponseEntity.ok(ApiResponse.success(loans, "Loans retrieved successfully"));
    }

    // GET LOAN BY ID (Librarian/Admin only)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoanById(@PathVariable Long id) {
        log.info("Fetching loan with ID: {}", id);
        LoanResponse loan = loanService.getLoanById(id);
        return ResponseEntity.ok(ApiResponse.success(loan, "Loan retrieved successfully"));
    }

    // GET LOANS BY USER (User can see their own, Librarian/Admin can see all)
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getLoansByUser(@PathVariable Long userId) {
        log.info("Fetching loans for user ID: {}", userId);
        // TODO: Add security check to ensure users can only see their own loans
        List<LoanResponse> loans = loanService.getLoansByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(loans, "User loans retrieved successfully"));
    }

    // GET ACTIVE LOANS (Librarian/Admin only)
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getActiveLoans() {
        log.info("Fetching active loans");
        List<LoanResponse> loans = loanService.getActiveLoans();
        return ResponseEntity.ok(ApiResponse.success(loans, "Active loans retrieved successfully"));
    }

    // GET OVERDUE LOANS (Librarian/Admin only)
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getOverdueLoans() {
        log.info("Fetching overdue loans");
        List<LoanResponse> loans = loanService.getOverdueLoans();
        return ResponseEntity.ok(ApiResponse.success(loans, "Overdue loans retrieved successfully"));
    }

    // CREATE LOAN (Librarian/Admin only)
    @PostMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<LoanResponse>> createLoan(@Valid @RequestBody CreateLoanRequest request) {
        log.info("Creating new loan for user ID: {} and book ID: {}", request.userId(), request.bookId());
        LoanResponse loan = loanService.createLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(loan, "Loan created successfully"));
    }

    // UPDATE LOAN (Extend due date - Librarian/Admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<LoanResponse>> updateLoan(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLoanRequest request) {
        log.info("Updating loan with ID: {}", id);
        LoanResponse loan = loanService.updateLoan(id, request);
        return ResponseEntity.ok(ApiResponse.success(loan, "Loan updated successfully"));
    }

    // RETURN LOAN (Librarian/Admin only)
    @PostMapping("/return")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<LoanResponse>> returnLoan(@Valid @RequestBody ReturnLoanRequest request) {
        log.info("Returning loan with ID: {}", request.loanId());
        LoanResponse loan = loanService.returnLoan(request);
        return ResponseEntity.ok(ApiResponse.success(loan, "Book returned successfully"));
    }

    // DELETE LOAN (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLoan(@PathVariable Long id) {
        log.info("Deleting loan with ID: {}", id);
        loanService.deleteLoan(id);
        return ResponseEntity.ok(ApiResponse.success("Loan deleted successfully"));
    }
}
