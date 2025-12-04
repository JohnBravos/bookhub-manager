package com.JohnBravos.bookhub_manager.service.impl;

import com.JohnBravos.bookhub_manager.core.enums.BookStatus;
import com.JohnBravos.bookhub_manager.core.enums.ReservationStatus;
import com.JohnBravos.bookhub_manager.core.exceptions.custom.*;
import com.JohnBravos.bookhub_manager.dto.Request.CreateReservationRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateReservationRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ReservationResponse;
import com.JohnBravos.bookhub_manager.mapper.ReservationMapper;
import com.JohnBravos.bookhub_manager.model.Book;
import com.JohnBravos.bookhub_manager.model.Reservation;
import com.JohnBravos.bookhub_manager.model.User;
import com.JohnBravos.bookhub_manager.repository.BookRepository;
import com.JohnBravos.bookhub_manager.repository.ReservationRepository;
import com.JohnBravos.bookhub_manager.repository.UserRepository;
import com.JohnBravos.bookhub_manager.service.IReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;

    private Sort buildSort(String sort) {
        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, sortBy);
    }

    @Override
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        log.debug("Creating new reservation for user ID: {} and book ID: {}", request.userId(), request.bookId());

        // Validate user and book exist
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BookNotFoundException(request.bookId()));

        // Business Rules Validation
        validateReservationCreation(user, book);

        // Manual builder για CREATE
        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .expiryDate(LocalDate.now().plusDays(7))
                .build();

        // Business Logic
        reservation.setReservationDate(LocalDate.now());
        reservation.setStatus(ReservationStatus.ACTIVE);

        // Calculate queue position
        int queuePosition = calculateQueuePosition(book.getId());

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Reservation created successfully with ID: {} (position in queue: {})", savedReservation.getId(), queuePosition);

        return reservationMapper.toResponse(savedReservation);

    }

    // Rules validation for Reservation Creation
    private void validateReservationCreation(User user, Book book) {
        // Rule 1: Book must exist and not be lost/under maintenance
        if (book.getStatus() == BookStatus.LOST || book.getStatus() == BookStatus.UNDER_MAINTENANCE) {
            throw new BookUnavailableException(book.getTitle(), 0, book.getTotalCopies());
        }

        // Rule 2: User cannot have multiple active reservations for the same book
        if (hasActiveReservation(user.getId(), book.getId())) {
            throw new ReservationNotAllowedException("User has already an active reservations for this book");
        }

        // Rule 3: Maximum active reservations per user
        int activeReservationsCount = getActiveReservationsCountByUser(user.getId());
        if (activeReservationsCount >= 3) {
            throw new ReservationNotAllowedException("User has reached the maximum active reservations limit");
        }

        log.debug("Reservation validation passed for user ID: {} and boo ID: {}", user.getId(), book.getId());
    }

    @Override
    public ReservationResponse getReservationById(Long id) {
        log.debug("Fetching reservation by ID: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        return reservationMapper.toResponse(reservation);
    }

    @Override
    public Page<ReservationResponse> getAllReservations(int page, int size, String sort) {
        log.debug("Fetching all reservations");
        Pageable pageable = PageRequest.of(page, size, buildSort(sort));
        return reservationRepository.findAll(pageable)
                .map(reservationMapper::toResponse);
    }

    @Override
    public Page<ReservationResponse> getReservationsByUser(Long userId, int page, int size, String sort) {
        log.debug("Fetching reservation for user ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        Pageable pageable = PageRequest.of(page, size, buildSort(sort));
        return reservationRepository.findByUserId(userId, pageable)
                .map(reservationMapper::toResponse);
    }

    @Override
    public List<ReservationResponse> getReservationsByBook(Long bookId) {
        log.debug("Fetching reservations for book ID: {}", bookId);
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }
        return reservationMapper.toResponseList(reservationRepository.findByBookId(bookId));
    }

    @Override
    public List<ReservationResponse> getReservationsByStatus(ReservationStatus status) {
        log.debug("Fetching reservations by status: {}", status);
        return reservationMapper.toResponseList(reservationRepository.findByStatus(status));
    }

    @Override
    public List<ReservationResponse> getActiveReservations() {
        log.debug("Fetching active reservations");
        return reservationMapper.toResponseList(reservationRepository.findByStatus(ReservationStatus.ACTIVE));
    }

    @Override
    public List<ReservationResponse> getExpiredReservations() {
        log.debug("Fetching expired reservations");
        return reservationMapper.toResponseList(reservationRepository.findExpiredReservations(LocalDate.now()));
    }

    public List<ReservationResponse> getReservationsExpiringSoon() {
        log.debug("Fetching reservations expiring soon");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1); // Expiring in the next 24 hours

        return reservationMapper.toResponseList(reservationRepository.findReservationsExpiringSoon(startDate, endDate));
    }

    @Override
    public List<ReservationResponse> getReservationQueueForBook(Long bookId) {
        log.debug("Fetching reservation queue for book ID: {}", bookId);
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }
        return reservationMapper.toResponseList(
                reservationRepository.findActiveReservationsByBookOrderByDate(bookId)
        );
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(Long reservationId, UpdateReservationRequest request) {
        log.info("Updating reservation with ID: {}", reservationId);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        // Only updating active reservations
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new ReservationNotAllowedException("Can only update active reservations");
        }

        // Αλλαγή βιβλίου αν δόθηκε νεο bookId
        if (request.bookId() != null) {
            Book newBook = bookRepository.findById(request.bookId())
                    .orElseThrow(() -> new BookNotFoundException(request.bookId()));
            reservation.setBook(newBook);
        }

        // Αλλαγή expiry date
        if (request.expiryDate() != null) {
            reservation.setExpiryDate(request.expiryDate());
        }

        Reservation updatedReservation = reservationRepository.save(reservation);

        log.info("Reservation updated successfully with ID: {}", updatedReservation.getId());
        return reservationMapper.toResponse(updatedReservation);
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long reservationId) {
        log.info("Cancelling reservation with ID: {}", reservationId);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation cancelledReservation = reservationRepository.save(reservation);

        log.info("Reservation cancelled successfully with ID: {}", reservationId);

        return reservationMapper.toResponse(cancelledReservation);
    }

    @Override
    @Transactional
    public ReservationResponse fulfillReservation(Long reservationId) {
        log.info("Fulfilling reservation with ID: {}", reservationId);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        reservation.setStatus(ReservationStatus.FULFILLED);
        Reservation fulfilledReservation = reservationRepository.save(reservation);

        log.info("Reservation fulfilled successfully with ID: {}", reservationId);
        return reservationMapper.toResponse(fulfilledReservation);
    }

    @Override
    @Transactional
    public void deleteReservation(Long reservationId) {
        log.info("Deleting reservation with ID: {}", reservationId);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        // Only allow deletion of cancelled or expired reservations
        if (reservation.getStatus() == ReservationStatus.ACTIVE) {
            throw new ReservationNotAllowedException("Cannot delete active reservations");
        }

        reservationRepository.delete(reservation);
        log.info("Reservation deleted successfully with ID: {}", reservationId);
    }

    @Override
    public boolean reservationExists(Long reservationId) {
        return reservationRepository.existsById(reservationId);
    }

    @Override
    public boolean hasActiveReservation(Long userId, Long bookId) {
        return reservationRepository.existsActiveReservationByUserAndBook(userId, bookId);
    }

    @Override
    public int getQueuePosition(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        return reservationRepository.findQueuePosition(
                reservation.getBook().getId(),
                reservation.getReservationDate()
        );
    }

    @Override
    public int getActiveReservationsCountByUser(Long userId) {
        return (int) reservationRepository.countByUserAndStatus(
                userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId)),
                ReservationStatus.ACTIVE
        );
    }

    private int calculateQueuePosition(Long bookId) {
        return reservationRepository.countActiveReservationsByBook(bookId);
    }
}
