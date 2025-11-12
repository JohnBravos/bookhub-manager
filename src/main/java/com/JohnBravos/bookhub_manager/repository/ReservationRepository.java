package com.JohnBravos.bookhub_manager.repository;

import com.JohnBravos.bookhub_manager.core.enums.ReservationStatus;
import com.JohnBravos.bookhub_manager.model.Book;
import com.JohnBravos.bookhub_manager.model.Reservation;
import com.JohnBravos.bookhub_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Reservations by user
    List<Reservation> findByUser(User user);

    // Reservations by book
    List<Reservation> findByBook(Book book);

    // Reservations by status
    List<Reservation> findByStatus(ReservationStatus status);

    // Active reservations of a user
    List<Reservation> findByUserAndStatus(User user, ReservationStatus status);

    // Active reservations of a book
    List<Reservation> findByBookAndStatus(Book book, ReservationStatus status);

    // Reservations by user ID
    List<Reservation> findByUserId(Long userId);

    // Reservations by book ID
    List<Reservation> findByBookId(Long bookId);

    // Active reservations by user ID
//    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    // Active reservations by book ID
    List<Reservation> findByBookIdAndStatus(Long bookId, ReservationStatus status);

    // Expired reservations (expiryDate < now and status = ACTIVE)
    @Query("SELECT r FROM Reservation r WHERE r.expiryDate < :currentDate AND r.status = 'ACTIVE'")
    List<Reservation> findExpiredReservations(@Param("currentDate") LocalDateTime currentDate);

    // Reservations that expires soon (next 24 ώρες)
    @Query("SELECT r FROM Reservation r WHERE r.expiryDate BETWEEN :startDate AND :endDate AND r.status = 'ACTIVE'")
    List<Reservation> findReservationsExpiringSoon(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    // Reservations that happened in specific range of time
    List<Reservation> findByReservationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Active reservation of a user for a specific book
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.book.id = :bookId AND r.status = 'ACTIVE'")
    Optional<Reservation> findActiveReservationByUserAndBook(@Param("userId") Long userId,
                                                             @Param("bookId") Long bookId);

    // Checks if a user has an active reservation for a book
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Reservation r WHERE r.user.id = :userId AND r.book.id = :bookId AND r.status = 'ACTIVE'")
    boolean existsActiveReservationByUserAndBook(@Param("userId") Long userId,
                                                 @Param("bookId") Long bookId);

    // Active reservations for a book (for waiting list)
    @Query("SELECT r FROM Reservation r WHERE r.book.id = :bookId AND r.status = 'ACTIVE' ORDER BY r.reservationDate ASC")
    List<Reservation> findActiveReservationsByBookOrderByDate(@Param("bookId") Long bookId);

    // Next reservation in a row for a book
    @Query("SELECT r FROM Reservation r WHERE r.book.id = :bookId AND r.status = 'ACTIVE' ORDER BY r.reservationDate ASC LIMIT 1")
    Optional<Reservation> findNextReservationInQueue(@Param("bookId") Long bookId);

    // Count reservations by status
    long countByStatus(ReservationStatus status);

    // Count active reservations for a user
    long countByUserAndStatus(User user, ReservationStatus status);

    // Count active reservations of a book
    long countByBookAndStatus(Book book, ReservationStatus status);

    // reservations that fulfilled (done)
    List<Reservation> findByStatusAndReservationDateBetween(ReservationStatus status,
                                                            LocalDateTime startDate,
                                                            LocalDateTime endDate);

    // Most recent reservations
    List<Reservation> findTop10ByOrderByReservationDateDesc();

    // Reservations ready to fulfill(book available again)
    @Query("SELECT r FROM Reservation r JOIN r.book b WHERE r.status = 'ACTIVE' AND b.availableCopies > 0")
    List<Reservation> findReservationsReadyForFulfillment();

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.book.id = :bookId AND r.status = 'ACTIVE'")
    int countActiveReservationsByBook(@Param("bookId") Long bookId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.book.id = :bookId AND r.status = 'ACTIVE' AND r.reservationDate < :reservationDate")
    int findQueuePosition(@Param("bookId") Long bookId, @Param("reservationDate") LocalDateTime reservationDate);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.status = :status")
    List<Reservation> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ReservationStatus status);
}
