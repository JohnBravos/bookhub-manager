package com.JohnBravos.bookhub_manager.controller;

import com.JohnBravos.bookhub_manager.core.enums.UserRole;
import com.JohnBravos.bookhub_manager.core.exceptions.custom.AccessDeniedException;
import com.JohnBravos.bookhub_manager.dto.Request.CreateReservationRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateReservationRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ApiResponse;
import com.JohnBravos.bookhub_manager.dto.Response.ReservationResponse;
import com.JohnBravos.bookhub_manager.model.User;
import com.JohnBravos.bookhub_manager.repository.UserRepository;
import com.JohnBravos.bookhub_manager.service.IReservationService;
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
@Tag(name = "Reservations", description = "Reservation Management API")
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get all reservations", description = "Retrieve all reservations (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reservations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<ReservationResponse>>> getAllReservations(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort format (e.g., id,asc)") @RequestParam(defaultValue = "id,asc") String sort,
            @Parameter(description = "Filter by status (ALL, PENDING, ACTIVE, READY, FULFILLED, CANCELLED)") @RequestParam(defaultValue = "ALL") String status) {
        log.info("Fetching all reservations");
        Page<ReservationResponse> reservations = reservationService.getAllReservations(page, size, sort, status);
        return ResponseEntity.ok(ApiResponse.success(reservations, "Reservations retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get reservation by ID", description = "Retrieve a specific reservation (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reservation retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reservation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservationById(
            @Parameter(description = "Reservation ID") @PathVariable Long id) {
        log.info("Fetching reservation with ID: {}", id);
        ReservationResponse reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation retrieved successfully"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reservations by user", description = "Retrieve user reservations (users can see their own, staff can see all)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User reservations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<ReservationResponse>>> getReservationsByUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort format (e.g., id,asc)") @RequestParam(defaultValue = "id,asc") String sort) {
        log.info("Fetching reservations for user ID: {}", userId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        if (!(currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.LIBRARIAN)) {
            if (!currentUser.getId().equals(userId)) {
                throw new AccessDeniedException("You can only access your own reservations");
            }
        }

        Page<ReservationResponse> reservations = reservationService.getReservationsByUser(userId, page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(reservations, "User reservations retrieved successfully"));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get active reservations", description = "Retrieve all active reservations (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active reservations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<ReservationResponse>>> getActiveReservations(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort format (e.g., id,asc)") @RequestParam(defaultValue = "id,asc") String sort) {
        log.info("Fetching active reservations");
        Page<ReservationResponse> reservations = reservationService.getActiveReservations(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(reservations, "Active reservations retrieved successfully"));
    }

    @GetMapping("/book/{bookId}/queue")
    @Operation(summary = "Get reservation queue for book", description = "Retrieve reservation queue for a specific book (public endpoint)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reservation queue retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getReservationQueueForBook(
            @Parameter(description = "Book ID") @PathVariable Long bookId) {
        log.info("Fetching reservation queue for book ID: {}", bookId);
        List<ReservationResponse> reservations = reservationService.getReservationQueueForBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(reservations, "Reservation queue retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create reservation", description = "Create a new reservation (authenticated users only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Reservation created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(
            @Parameter(description = "Reservation creation request") @Valid @RequestBody CreateReservationRequest request) {
        log.info("Creating new reservation for user ID: {} and book ID: {}", request.userId(), request.bookId());
        ReservationResponse reservation = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(reservation, "Reservation created successfully"));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Approve pending reservation", description = "Approve a pending reservation (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reservation approved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reservation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<ReservationResponse>> approveReservation(
            @Parameter(description = "Reservation ID") @PathVariable Long id) {
        log.info("Approving reservation with ID: {}", id);
        ReservationResponse reservation = reservationService.approveReservation(id);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation approved successfully"));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Reject pending reservation", description = "Reject a pending reservation (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reservation rejected successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reservation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<ReservationResponse>> rejectReservation(
            @Parameter(description = "Reservation ID") @PathVariable Long id) {
        log.info("Rejecting reservation with ID: {}", id);
        ReservationResponse reservation = reservationService.rejectReservation(id);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation rejected successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Update reservation", description = "Update reservation details (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reservation updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reservation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<ReservationResponse>> updateReservation(
            @Parameter(description = "Reservation ID") @PathVariable Long id,
            @Parameter(description = "Reservation update request") @Valid @RequestBody UpdateReservationRequest request) {
        log.info("Updating reservation with ID: {}", id);
        ReservationResponse reservation = reservationService.updateReservation(id, request);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation updated successfully"));
    }

    @PostMapping("/{id}/ready")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Mark reservation as ready", description = "Mark a reservation as ready for pickup (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reservation marked as ready successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reservation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<ReservationResponse>> markReservationReady(
            @Parameter(description = "Reservation ID") @PathVariable Long id) {
        log.info("Marking reservation as READY with ID: {}", id);
        ReservationResponse reservation = reservationService.markReservationReady(id);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation marked as ready successfully"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel reservation", description = "Cancel a reservation (users can cancel their own, staff can cancel any)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reservation cancelled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reservation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<ReservationResponse>> cancelReservation(
            @Parameter(description = "Reservation ID") @PathVariable Long id) {
        log.info("Cancelling reservation with ID: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        ReservationResponse reservationToCancel = reservationService.getReservationById(id);
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isLibrarian = currentUser.getRole() == UserRole.LIBRARIAN;

        if (!isAdmin && !isLibrarian) {
            if (!reservationToCancel.user().id().equals(currentUser.getId())) {
                throw new AccessDeniedException("You can only cancel your own reservations");
            }
        }

        ReservationResponse reservation = reservationService.cancelReservation(id);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation cancelled successfully"));
    }

    @PostMapping("/{id}/fulfill")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Fulfill reservation", description = "Fulfill a reservation when book becomes available (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reservation fulfilled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reservation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<ReservationResponse>> fulfillReservation(
            @Parameter(description = "Reservation ID") @PathVariable Long id) {
        log.info("Fulfilling reservation with ID: {}", id);
        ReservationResponse reservation = reservationService.fulfillReservation(id);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation fulfilled successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete reservation", description = "Delete a reservation record (ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reservation deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reservation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Void>> deleteReservation(
            @Parameter(description = "Reservation ID") @PathVariable Long id) {
        log.info("Deleting reservation with ID: {}", id);
        reservationService.deleteReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation deleted successfully"));
    }
}
