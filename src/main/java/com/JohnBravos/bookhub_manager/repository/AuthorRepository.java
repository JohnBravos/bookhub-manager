package com.JohnBravos.bookhub_manager.repository;

import com.JohnBravos.bookhub_manager.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Author by first name και last name (exact match)
    Optional<Author> findByFirstNameAndLastName(String firstName, String lastName);

    // Authors by first name (contains)
    List<Author> findAuthorByFirstNameContainingIgnoreCase(String firstName);

    // Authors by last name (contains)
    List<Author> findAuthorByLastNameContainingIgnoreCase(String lastName);

    Page<Author> findAll(Pageable pageable);

    // Authors by nationality
    List<Author> findByNationality(String nationality);

    // Authors by name (first or last)
    @Query("SELECT a FROM Author a WHERE " +
            "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Author> findByNameContaining(@Param("name") String name);

    // Authors with books
    @Query("SELECT DISTINCT a FROM Author a JOIN a.books b WHERE b IS NOT NULL")
    List<Author> findAuthorsWithBooks();

    // Authors with no books
    @Query("SELECT a FROM Author a WHERE a.books IS EMPTY")
    List<Author> findAuthorsWithoutBooks();

    // Count books per authors
    @Query("SELECT a, COUNT(b) FROM Author a LEFT JOIN a.books b GROUP BY a")
    List<Object[]> countBooksPerAuthor();

    // Authors with most books (top 10)
    @Query("SELECT a, COUNT(b) as bookCount FROM Author a JOIN a.books b GROUP BY a ORDER BY bookCount DESC LIMIT 10")
    List<Object[]> findTopAuthorsByBookCount();

    // Authors by book title
    @Query("SELECT a FROM Author a JOIN a.books b WHERE b.title = :bookTitle")
    List<Author> findByBookTitle(@Param("bookTitle") String bookTitle);

    // Checks if author exists by name
    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    // Authors by book ID
    @Query("SELECT a FROM Author a JOIN a.books b WHERE b.id = :bookId")
    List<Author> findByBookId(@Param("bookId") Long bookId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Author a JOIN a.books b WHERE a.id = :authorId")
    boolean hasBooks(@Param("authorId") Long authorId);
}
