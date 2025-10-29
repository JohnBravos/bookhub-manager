package com.JohnBravos.bookhub_manager.repository;

import com.JohnBravos.bookhub_manager.core.enums.BookStatus;
import com.JohnBravos.bookhub_manager.model.Author;
import com.JohnBravos.bookhub_manager.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    // Book by ISBN
    Optional<Book> findByIsbn(String isbn);

    // Books by title (exact match)
    List<Book> findByTitle(String title);

    // Books by title (contains)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Books by author name
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.firstName LIKE %:name% OR a.lastName LIKE %:name%")
    List<Book> findByAuthorNameContaining(@Param("name") String name);

    // Books by status
    List<Book> findByStatus(BookStatus status);

    // Available Copies (availableCopies > 0)
    List<Book> findByAvailableCopiesGreaterThan(int count);

    // Zero availability copies
    List<Book> findByAvailableCopies(int availableCopies);

    // Books by publication year
    List<Book> findByPublicationYear(int publicationYear);

    // Books by publisher
    List<Book> findByPublisherContainingIgnoreCase(String publisher);

    // Books by multiple criteria (search)
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.genre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.publisher) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> searchBooks(@Param("query") String query);

    // Check if book exists by ISBN
    boolean existsByIsbn(String isbn);

    // Count books by status
    long countByStatus(BookStatus status);

    // Find books with active loans
    @Query("SELECT DISTINCT b FROM Book b JOIN b.loans l WHERE l.status = 'ACTIVE'")
    List<Book> findBooksWithActiveLoans();

    // Books available to loan
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 AND b.status = 'AVAILABLE'")
    List<Book> findAvailableBooksForLoan();

    // Books by author ID
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorId")
    List<Book> findByAuthorId(@Param("authorId") Long authorId);
}
