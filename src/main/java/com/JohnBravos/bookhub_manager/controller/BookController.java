package com.JohnBravos.bookhub_manager.controller;

import com.JohnBravos.bookhub_manager.core.enums.BookStatus;
import com.JohnBravos.bookhub_manager.dto.Request.CreateBookRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateBookRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ApiResponse;
import com.JohnBravos.bookhub_manager.dto.Response.BookDetailsResponse;
import com.JohnBravos.bookhub_manager.dto.Response.BookResponse;
import com.JohnBravos.bookhub_manager.dto.Response.SearchBookRequest;
import com.JohnBravos.bookhub_manager.service.IBookService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "Books", description = "Book Management API")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final IBookService bookService;

    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieve all books with pagination support")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getAllBooks(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort format (e.g., id,asc)") @RequestParam(defaultValue = "id,asc") String sort) {
        Page<BookResponse> booksPage = bookService.getAllBooks(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(booksPage, "Books fetched successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieve a specific book by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        log.info("Fetching book with ID: {}", id);
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(ApiResponse.success(book, "Book retrieved successfully"));
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Get book details", description = "Retrieve detailed information for a specific book")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book details retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<ApiResponse<BookDetailsResponse>> getBookDetails(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        log.info("Fetching detailed information for book ID: {}", id);
        BookDetailsResponse bookDetails = bookService.getBookDetails(id);
        return ResponseEntity.ok(ApiResponse.success(bookDetails, "Book details retrieved successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search for books with optional filters (query, genre, author, publication year)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchBooks(
            @Parameter(description = "Search query") @RequestParam(required = false) String query,
            @Parameter(description = "Genre filter") @RequestParam(required = false) String genre,
            @Parameter(description = "Author filter") @RequestParam(required = false) String author,
            @Parameter(description = "Publication year from") @RequestParam(required = false) Integer publicationYearFrom,
            @Parameter(description = "Publication year to") @RequestParam(required = false) Integer publicationYearTo,
            @Parameter(description = "Show only available books") @RequestParam(required = false) Boolean availableOnly) {
        log.info("Searching books with query: {}, genre: {}, author: {}", query, genre, author);
        SearchBookRequest searchRequest = new SearchBookRequest(
                query, genre, author, publicationYearFrom,
                publicationYearTo, availableOnly, null, null);
        List<BookResponse> books = bookService.searchBooks(searchRequest);
        return ResponseEntity.ok(ApiResponse.success(books, "Search completed successfully"));
    }

    @GetMapping("/available")
    @Operation(summary = "Get available books", description = "Retrieve all books that are currently available for borrowing")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Available books retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAvailableBooks() {
        log.info("Fetching available books");
        List<BookResponse> books = bookService.getAvailableBooks();
        return ResponseEntity.ok(ApiResponse.success(books, "Available books retrieved successfully"));
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get books by author", description = "Retrieve all books written by a specific author")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Books by author retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBooksByAuthor(
            @Parameter(description = "Author ID") @PathVariable Long authorId) {
        log.info("Fetching books by author ID: {}", authorId);
        List<BookResponse> books = bookService.getBooksByAuthor(authorId);
        return ResponseEntity.ok(ApiResponse.success(books, "Books by author retrieved successfully"));
    }

    @GetMapping("/genre/{genre}")
    @Operation(summary = "Get books by genre", description = "Retrieve all books in a specific genre")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Books by genre retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBooksByGenre(
            @Parameter(description = "Genre name") @PathVariable String genre) {
        log.info("Fetching books by genre: {}", genre);
        List<BookResponse> books = bookService.getBooksByGenre(genre);
        return ResponseEntity.ok(ApiResponse.success(books, "Books by genre retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Create new book", description = "Create a new book (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Book created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
            @Parameter(description = "Book creation request") @Valid @RequestBody CreateBookRequest request) {
        log.info("Creating new book: {}", request.title());
        BookResponse book = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(book, "Book created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Update book", description = "Update an existing book (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Parameter(description = "Book update request") @Valid @RequestBody UpdateBookRequest request) {
        log.info("Updating book with ID: {}", id);
        BookResponse book = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success(book, "Book updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Update book status", description = "Update the status of a book (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<BookResponse>> updateBookStatus(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Parameter(description = "New book status") @RequestParam BookStatus newStatus) {
        log.info("Updating status for book ID: {} to {}", id, newStatus);
        BookResponse book = bookService.updateBookStatus(id, newStatus);
        return ResponseEntity.ok(ApiResponse.success(book, "Book status updated successfully"));
    }

    @PatchMapping("/{id}/updateCopies")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Update book copies", description = "Update the number of copies for a book (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book copies updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<BookResponse>> updateBookCopies(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Parameter(description = "Update request with newTotalCopies field") @RequestBody Map<String, Integer> updates) {
        if (updates == null || !updates.containsKey("newTotalCopies")) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Missing field 'newTotalCopies' in request body"));
        }
        int newTotalCopies = updates.get("newTotalCopies");
        BookResponse updatedBook = bookService.updateBookCopies(id, newTotalCopies);
        return ResponseEntity.ok(ApiResponse.success(updatedBook, "Book copies updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete book", description = "Delete a book (ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Void>> deleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        log.info("Deleting book with ID: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully"));
    }
}
