package com.JohnBravos.bookhub_manager.repository;

import com.JohnBravos.bookhub_manager.core.enums.LoanStatus;
import com.JohnBravos.bookhub_manager.model.Book;
import com.JohnBravos.bookhub_manager.model.Loan;
import com.JohnBravos.bookhub_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    // All loans by a user
    List<Loan> findByUser(User user);

    // All loans of a book
    List<Loan> findByBook(Book book);

    // Loans by status
    List<Loan> findByStatus(LoanStatus status);

    // Active loans of a user
    List<Loan> findByUserAndStatus(User user, LoanStatus status);

    // Active loans of a book
    List<Loan> findByBookAndStatus(Book book, LoanStatus status);

    // Loans by User ID
    List<Loan> findByUserId(Long userId);

    // Loans by book ID
    List<Loan> findByBookId(Long bookId);

    // Active loans by User ID
//    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);

    // Active loans by book ID
    List<Loan> findByBookIdAndStatus(Long bookId, LoanStatus status);

    // Overdue loans (dueDate < τώρα και status = ACTIVE)
    @Query("SELECT l FROM Loan l WHERE l.dueDate < :currentDate AND l.status = 'ACTIVE'")
    List<Loan> findOverdueLoans(@Param("currentDate") LocalDateTime currentDate);

    // Loans that expires in specific date
    List<Loan> findByDueDate(LocalDateTime dueDate);

    // Loans that expires in a range of dates
    List<Loan> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Loans that happened is a range of dates
    List<Loan> findByLoanDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Loans that returned is range of dates
    List<Loan> findByReturnDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Checks if a user has an active loan for a book
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
            "FROM Loan l WHERE l.user.id = :userId AND l.book.id = :bookId AND l.status = 'ACTIVE'")
    boolean existsActiveLoanByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // Count loans by status
    long countByStatus(LoanStatus status);

    // Count active loans of a user
    long countByUserAndStatus(User user, LoanStatus status);

    // Most recent loans (by loan date)
    List<Loan> findTop10ByOrderByLoanDateDesc();

    // Loans that must be returned soon (next 3 days)
    @Query("SELECT l FROM Loan l WHERE l.dueDate BETWEEN :startDate AND :endDate AND l.status = 'ACTIVE'")
    List<Loan> findLoansDueSoon(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    // Last active loan of a user for a specific book
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.book.id = :bookId AND l.status = 'ACTIVE' ORDER BY l.loanDate DESC LIMIT 1")
    Optional<Loan> findLatestActiveLoanByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // Statistics - average days time of loans
    @Query("SELECT AVG(DATEDIFF(l.returnDate, l.loanDate)) FROM Loan l WHERE l.returnDate IS NOT NULL")
    Double findAverageLoanDuration();

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND l.status = :status")
    int countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") LoanStatus status);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);

    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.status = :status")
    List<Loan> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") LoanStatus status);
}
