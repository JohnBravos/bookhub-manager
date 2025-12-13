package com.JohnBravos.bookhub_manager.controller;

import com.JohnBravos.bookhub_manager.core.exceptions.custom.AccessDeniedException;
import com.JohnBravos.bookhub_manager.dto.Request.CreateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.ReturnLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ApiResponse;
import com.JohnBravos.bookhub_manager.dto.Response.LoanResponse;
import com.JohnBravos.bookhub_manager.model.User;
import com.JohnBravos.bookhub_manager.core.enums.UserRole;
import com.JohnBravos.bookhub_manager.repository.UserRepository;
import com.JohnBravos.bookhub_manager.service.ILoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Loans", description = "Loan Management API")
@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final ILoanService loanService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get all loans", description = "Retrieve all loans (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loans retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<LoanResponse>>> getAllLoans(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort format (e.g., id,asc)") @RequestParam(defaultValue = "id,asc") String sort,
            @Parameter(description = "Filter by status (ALL, ACTIVE, RETURNED, OVERDUE)") @RequestParam(defaultValue = "ALL") String status) {
        log.info("Fetching all loans");
        Page<LoanResponse> loans = loanService.getAllLoans(page, size, sort, status);
        return ResponseEntity.ok(ApiResponse.success(loans, "Loans retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get loan by ID", description = "Retrieve a specific loan (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<LoanResponse>> getLoanById(
            @Parameter(description = "Loan ID") @PathVariable Long id) {
        log.info("Fetching loan with ID: {}", id);
        LoanResponse loan = loanService.getLoanById(id);
        return ResponseEntity.ok(ApiResponse.success(loan, "Loan retrieved successfully"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get loans by user", description = "Retrieve user loans (users can see their own, staff can see all)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User loans retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<LoanResponse>>> getLoansByUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort format (e.g., id,asc)") @RequestParam(defaultValue = "id,asc") String sort) {
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

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get active loans", description = "Retrieve all active loans (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active loans retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<LoanResponse>>> getActiveLoans(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort format (e.g., id,asc)") @RequestParam(defaultValue = "id,asc") String sort) {
        log.info("Fetching active loans");
        Page<LoanResponse> loans = loanService.getActiveLoans(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(loans, "Active loans retrieved successfully"));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get overdue loans", description = "Retrieve all overdue loans (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Overdue loans retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getOverdueLoans() {
        log.info("Fetching overdue loans");
        List<LoanResponse> loans = loanService.getOverdueLoans();
        return ResponseEntity.ok(ApiResponse.success(loans, "Overdue loans retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN', 'MEMBER')")
    @Operation(summary = "Create loan", description = "Create a new loan (LIBRARIAN, ADMIN, or MEMBER)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Loan created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<LoanResponse>> createLoan(
            @Parameter(description = "Loan creation request") @Valid @RequestBody CreateLoanRequest request) {
        log.info("Creating new loan for user ID: {} and book ID: {}", request.userId(), request.bookId());
        LoanResponse loan = loanService.createLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(loan, "Loan created successfully"));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Approve loan request", description = "Approve a pending loan request (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan request approved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<LoanResponse>> approveLoan(
            @Parameter(description = "Loan ID") @PathVariable Long id) {
        log.info("Approving loan request with ID: {}", id);
        LoanResponse loan = loanService.approveLoan(id);
        return ResponseEntity.ok(ApiResponse.success(loan, "Loan request approved successfully"));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Reject loan request", description = "Reject a pending loan request (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan request rejected successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<LoanResponse>> rejectLoan(
            @Parameter(description = "Loan ID") @PathVariable Long id) {
        log.info("Rejecting loan request with ID: {}", id);
        LoanResponse loan = loanService.rejectLoan(id);
        return ResponseEntity.ok(ApiResponse.success(loan, "Loan request rejected successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Update loan", description = "Update loan details like due date (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<LoanResponse>> updateLoan(
            @Parameter(description = "Loan ID") @PathVariable Long id,
            @Parameter(description = "Loan update request") @Valid @RequestBody UpdateLoanRequest request) {
        log.info("Updating loan with ID: {}", id);
        LoanResponse loan = loanService.updateLoan(id, request);
        return ResponseEntity.ok(ApiResponse.success(loan, "Loan updated successfully"));
    }

    @PostMapping("/{id}/renew")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Renew loan", description = "Renew a loan and extend its due date (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan renewed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<LoanResponse>> renewLoan(
            @Parameter(description = "Loan ID") @PathVariable Long id) {
        log.info("Renewing loan with ID: {}", id);
        LoanResponse loan = loanService.renewLoan(id);
        return ResponseEntity.ok(ApiResponse.success(loan, "Loan renewed successfully"));
    }

    @PostMapping("/return")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Return loan", description = "Record a book return (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<LoanResponse>> returnLoan(
            @Parameter(description = "Return loan request") @Valid @RequestBody ReturnLoanRequest request) {
        log.info("Returning loan with ID: {}", request.loanId());
        LoanResponse loan = loanService.returnLoan(request);
        return ResponseEntity.ok(ApiResponse.success(loan, "Book returned successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete loan", description = "Delete a loan record (ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Void>> deleteLoan(
            @Parameter(description = "Loan ID") @PathVariable Long id) {
        log.info("Deleting loan with ID: {}", id);
        loanService.deleteLoan(id);
        return ResponseEntity.ok(ApiResponse.success("Loan deleted successfully"));
    }
}
