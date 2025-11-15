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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;
    private final UserRepository userRepository;

    // GET ALL RESERVATIONS (Librarian/Admin only)
    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAllReservations() {
        log.info("Fetching all reservations");
        List<ReservationResponse> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(ApiResponse.success(reservations, "Reservations retrieved successfully"));
    }

    // GET RESERVATION BY ID (Librarian/Admin only)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservationById(@PathVariable Long id) {
        log.info("Fetching reservation with ID: {}", id);
        ReservationResponse reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation retrieved successfully"));
    }

    // GET RESERVATIONS BY USER (User can see their own, Librarian/Admin can see all)
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getReservationsByUser(@PathVariable Long userId) {
        log.info("Fetching reservations for user ID: {}", userId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (!(currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.LIBRARIAN)) {
            if (!currentUser.getId().equals(userId)) {
                throw new AccessDeniedException("You can only access your own loans");
            }
        }

        List<ReservationResponse> reservations = reservationService.getReservationsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(reservations, "User reservations retrieved successfully"));
    }

    // GET ACTIVE RESERVATIONS (Librarian/Admin only)
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getActiveReservations() {
        log.info("Fetching active reservations");
        List<ReservationResponse> reservations = reservationService.getActiveReservations();
        return ResponseEntity.ok(ApiResponse.success(reservations, "Active reservations retrieved successfully"));
    }

    // GET RESERVATION QUEUE FOR BOOK (Public)
    @GetMapping("/book/{bookId}/queue")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getReservationQueueForBook(@PathVariable Long bookId) {
        log.info("Fetching reservation queue for book ID: {}", bookId);
        List<ReservationResponse> reservations = reservationService.getReservationQueueForBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(reservations, "Reservation queue retrieved successfully"));
    }

    // CREATE RESERVATION (Authenticated users)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(@Valid @RequestBody CreateReservationRequest request) {
        log.info("Creating new reservation for user ID: {} and book ID: {}", request.userId(), request.bookId());
        ReservationResponse reservation = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(reservation, "Reservation created successfully"));
    }

    // UPDATE RESERVATION (Librarian/Admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReservationResponse>> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReservationRequest request) {
        log.info("Updating reservation with ID: {}", id);
        ReservationResponse reservation = reservationService.updateReservation(id, request);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation updated successfully"));
    }

    // CANCEL RESERVATION (User can cancel their own, Librarian/Admin can cancel any)
    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancelReservation(@PathVariable Long id) {
        log.info("Cancelling reservation with ID: {}", id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User currentUser = userRepository.findByEmail(username)
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

    // FULFILL RESERVATION (Librarian/Admin only - when book becomes available)
    @PostMapping("/{id}/fulfill")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReservationResponse>> fulfillReservation(@PathVariable Long id) {
        log.info("Fulfilling reservation with ID: {}", id);
        ReservationResponse reservation = reservationService.fulfillReservation(id);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation fulfilled successfully"));
    }

    // DELETE RESERVATION (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(@PathVariable Long id) {
        log.info("Deleting reservation with ID: {}", id);
        reservationService.deleteReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation deleted successfully"));
    }
}
