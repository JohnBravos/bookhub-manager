package com.JohnBravos.bookhub_manager.controller;

import com.JohnBravos.bookhub_manager.core.exceptions.custom.AccessDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.JohnBravos.bookhub_manager.model.User;
import com.JohnBravos.bookhub_manager.core.enums.UserRole; // ή όπως λέγεται το enum σου
import com.JohnBravos.bookhub_manager.dto.Request.CreateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.ReturnLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ApiResponse;
import com.JohnBravos.bookhub_manager.dto.Response.LoanResponse;
import com.JohnBravos.bookhub_manager.repository.UserRepository;
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
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final ILoanService loanService;
    private final UserRepository userRepository;

    // GET ALL LOANS (Librarian/Admin only)
    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<LoanResponse>>> getAllLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(defaultValue = "ALL") String status
    ) {
        log.info("Fetching all loans");
        Page<LoanResponse> loans = loanService.getAllLoans(page, size, sort, status);
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
    public ResponseEntity<ApiResponse<Page<LoanResponse>>> getLoansByUser(
            @PathVariable Long userId,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size,
            @RequestParam (defaultValue = "id,asc") String sort
            ) {
        log.info("Fetching loans for user ID: {}", userId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (!(currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.LIBRARIAN)) {
            if (!currentUser.getId().equals(userId)) {
                throw new AccessDeniedException("You can only access your own loans");
            }
        }

        Page<LoanResponse> loans = loanService.getLoansByUser(userId, page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(loans, "User loans retrieved successfully"));
    }

    // GET ACTIVE LOANS (Librarian/Admin only)
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<LoanResponse>>> getActiveLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        log.info("Fetching active loans");
        Page<LoanResponse> loans = loanService.getActiveLoans(page, size, sort);
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
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN', 'MEMBER')")
    public ResponseEntity<ApiResponse<LoanResponse>> createLoan(@Valid @RequestBody CreateLoanRequest request) {
        log.info("Creating new loan for user ID: {} and book ID: {}", request.userId(), request.bookId());
        LoanResponse loan = loanService.createLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(loan, "Loan created successfully"));
    }

    // APPROVE PENDING LOAN REQUEST (Librarian/Admin only)
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<LoanResponse>> approveLoan(
            @PathVariable Long id
    ) {
        log.info("Approving loan request with ID: {}", id);
        LoanResponse loan = loanService.approveLoan(id);
        return ResponseEntity.ok(ApiResponse.success(loan, "Loan request approved successfully"));
    }

    // REJECT PENDING LOAN REQUEST (Librarian/Admin only)
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<LoanResponse>> rejectLoan(
            @PathVariable Long id
    ) {
        log.info("Rejecting loan request with ID: {}", id);
        LoanResponse loan = loanService.rejectLoan(id);
        return ResponseEntity.ok(ApiResponse.success(loan, "Loan request rejected successfully"));
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

    // Στο LoanController.java - πρόσθεσε αυτή τη method:

    @PostMapping("/{id}/renew")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<LoanResponse>> renewLoan(@PathVariable Long id) {
        log.info("Renewing loan with ID: {}", id);
        LoanResponse loan = loanService.renewLoan(id);
        return ResponseEntity.ok(ApiResponse.success(loan, "Loan renewed successfully"));
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
