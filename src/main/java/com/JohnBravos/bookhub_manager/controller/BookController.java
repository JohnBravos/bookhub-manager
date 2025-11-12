package com.JohnBravos.bookhub_manager.controller;

import com.JohnBravos.bookhub_manager.core.enums.BookStatus;
import com.JohnBravos.bookhub_manager.dto.Request.CreateBookRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateBookRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ApiResponse;
import com.JohnBravos.bookhub_manager.dto.Response.BookDetailsResponse;
import com.JohnBravos.bookhub_manager.dto.Response.BookResponse;
import com.JohnBravos.bookhub_manager.dto.Response.SearchBookRequest;
import com.JohnBravos.bookhub_manager.service.IBookService;
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
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final IBookService bookService;

    // GET ALL BOOKS (Public)
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        log.info("Fetching all books");
        List<BookResponse> books = bookService.getAllBooks();
        return ResponseEntity.ok(ApiResponse.success(books, "Books retrieved successfully"));
    }

    // GET BOOK BY ID (PUBLIC)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable Long id) {
        log.info("Fetching book with ID: {}", id);
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(ApiResponse.success(book, "Book retrieved successfully"));
    }

    // GET BOOK DETAILS (Public)
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<BookDetailsResponse>> getBookDetails(@PathVariable Long id) {
        log.info("Fetching detailed information for book ID: {}", id);
        BookDetailsResponse bookDetails = bookService.getBookDetails(id);
        return ResponseEntity.ok(ApiResponse.success(bookDetails, "Book details" +
                " retrieved successfully"));
    }

    // SEARCH BOOKS (Public)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer publicationYearFrom,
            @RequestParam(required = false) Integer publicationYearTo,
            @RequestParam(required = false) Boolean availableOnly) {

        log.info("Searching books with query: {}, genre: {}, author: {}", query, genre, author);

        SearchBookRequest searchRequest = new SearchBookRequest(
                query, genre, author, publicationYearFrom,
                publicationYearTo, availableOnly, null, null
        );

        List<BookResponse> books = bookService.searchBooks(searchRequest);
        return ResponseEntity.ok(ApiResponse.success(books, "Search completed successfully"));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAvailableBooks() {
        log.info("Fetching available books");
        List<BookResponse> books = bookService.getAvailableBooks();
        return ResponseEntity.ok(ApiResponse.success(books, "Available books" +
                " retrieved successfully"));
    }

    // GET BOOKS BY AUTHOR (Public)
    @GetMapping("/author/{authorId}")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBooksByAuthor(@PathVariable Long authorId) {
        log.info("Fetching books by author ID: {}", authorId);
        List<BookResponse> books = bookService.getBooksByAuthor(authorId);
        return ResponseEntity.ok(ApiResponse.success(books, "Books by author retrieved successfully"));
    }

    // GET BOOKS BY GENRE (Public)
    @GetMapping("/genre/{genre}")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBooksByGenre(@PathVariable String genre) {
        log.info("Fetching books by genre: {}", genre);
        List<BookResponse> books = bookService.getBooksByGenre(genre);
        return ResponseEntity.ok(ApiResponse.success(books, "Books by genre retrieved successfully"));
    }

    // CREATE BOOK (Librarian/Admin only)
    @PostMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @RequestBody CreateBookRequest request) {
        log.info("Creating new book: {}", request.title());
        BookResponse book = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(book, "Book created successfully"));
    }

    // UPDATE BOOK (Librarian/Admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {
        log.info("Updating book with ID: {}", id);
        BookResponse book = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success(book, "Book updated successfully"));
    }

    // UPDATE BOOK STATUS (Librarian/Admin only)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBookStatus(
            @PathVariable Long id,
            @RequestParam BookStatus newStatus) {
        log.info("Updating status for book ID: {} to {}", id, newStatus);
        BookResponse book = bookService.updateBookStatus(id, newStatus);
        return ResponseEntity.ok(ApiResponse.success(book, "Book status updated successfully"));
    }

    // UPDATE BOOK COPIES (Librarian/Admin only)
    @PatchMapping("/{id}/copies")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBookCopies(
            @PathVariable Long id,
            @RequestParam int newTotalCopies) {
        log.info("Updating copies for book ID: {} to {}", id, newTotalCopies);
        BookResponse book = bookService.updateBookCopies(id, newTotalCopies);
        return ResponseEntity.ok(ApiResponse.success(book, "Book copies updated successfully"));
    }

    // DELETE BOOK (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        log.info("Deleting book with ID: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully"));
    }
}
