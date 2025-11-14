package com.JohnBravos.bookhub_manager.service;

import com.JohnBravos.bookhub_manager.core.enums.BookStatus;
import com.JohnBravos.bookhub_manager.dto.Request.CreateBookRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateBookRequest;
import com.JohnBravos.bookhub_manager.dto.Response.BookDetailsResponse;
import com.JohnBravos.bookhub_manager.dto.Response.BookResponse;
import com.JohnBravos.bookhub_manager.dto.Response.SearchBookRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBookService {

    // CREATE
    BookResponse createBook(CreateBookRequest request);

    // READ
    BookResponse getBookById(Long id);
    BookDetailsResponse getBookDetails(Long bookId);
    List<BookResponse> getAllBooks();
    Page<BookResponse> getAllBooks(int page, int size, String sort);
    List<BookResponse> getAvailableBooks();
    List<BookResponse> searchBooks(SearchBookRequest searchRequest);
    List<BookResponse> getBooksByAuthor(Long authorId);
    List<BookResponse> getBooksByGenre(String genre);

    // UPDATE
    BookResponse updateBook(Long booId, UpdateBookRequest request);
    BookResponse updateBookStatus(Long bookId, BookStatus newStatus);
    BookResponse updateBookCopies(Long bookId, int newTotalCopies);

    // DELETE
    void deleteBook(Long bookId);

    // UTILITY
    boolean bookExists(Long bookId);
    boolean isIsbnAvailable(String isbn);
    int getAvailableCopies(Long bookId);
}
