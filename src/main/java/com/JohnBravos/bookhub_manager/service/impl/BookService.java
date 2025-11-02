package com.JohnBravos.bookhub_manager.service.impl;

import com.JohnBravos.bookhub_manager.core.enums.BookStatus;
import com.JohnBravos.bookhub_manager.core.exceptions.custom.AuthorNotFoundException;
import com.JohnBravos.bookhub_manager.core.exceptions.custom.BookNotFoundException;
import com.JohnBravos.bookhub_manager.core.exceptions.custom.DuplicateIsbnException;
import com.JohnBravos.bookhub_manager.dto.Request.CreateBookRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateBookRequest;
import com.JohnBravos.bookhub_manager.dto.Response.BookDetailsResponse;
import com.JohnBravos.bookhub_manager.dto.Response.BookResponse;
import com.JohnBravos.bookhub_manager.dto.Response.SearchBookRequest;
import com.JohnBravos.bookhub_manager.mapper.BookMapper;
import com.JohnBravos.bookhub_manager.model.Author;
import com.JohnBravos.bookhub_manager.model.Book;
import com.JohnBravos.bookhub_manager.repository.AuthorRepository;
import com.JohnBravos.bookhub_manager.repository.BookRepository;
import com.JohnBravos.bookhub_manager.service.IBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService implements IBookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        log.info("Creating new book: {}", request.title());

        // Validate ISBN uniqueness
        if (!isIsbnAvailable(request.isbn())) {
            throw new DuplicateIsbnException(request.isbn());
        }

        // Validate authors exist
        List<Author> authors = authorRepository.findAllById(request.authorIds());
        if (authors.size() != request.authorIds().size()) {
            throw new AuthorNotFoundException("One or more authors not found");
        }

        // Create book entity
        Book book = Book.builder()
                .title(request.title())
                .isbn(request.isbn())
                .publisher(request.publisher())
                .publicationYear(request.publicationYear())
                .genre(request.genre())
                .description(request.description())
                .totalCopies(request.totalCopies())
                .authors(authors)
                .build();

        // ✅ Business Logic ΕΔΩ - ξεκάθαρο!
        book.setAvailableCopies(request.totalCopies());
        book.setStatus(request.totalCopies() > 0 ? BookStatus.AVAILABLE : BookStatus.BORROWED);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }


        @Override
        public BookResponse getBookById(Long id) {
            log.debug("Fetching book by ID: {}", id);
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new BookNotFoundException(id));
            return bookMapper.toResponse(book);
        }

        @Override
        public BookDetailsResponse getBookDetails(Long bookId) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException(bookId));

            return BookDetailsResponse.builder()
                    .book(bookMapper.toResponse(book))
                    .activeLoansCount(0)
                    .pendingReservationsCount(0)
                    .isAvailable(book.getAvailableCopies() > 0)
                    .estimatedWaitDays(0)
                    .totalTimesBorrowed(0)
                    .averageLoanDuration(0.0)
                    .build();
        }

        @Override
        public List<BookResponse> getAllBooks() {
            log.debug("Fetching all books");
            return bookMapper.toResponseList(bookRepository.findAll());
        }

        @Override
        public List<BookResponse> getAvailableBooks() {
            log.debug("Fetching available books");
            return bookMapper.toResponseList(bookRepository.findAvailableBooksForLoan());
        }

        @Override
        public List<BookResponse> searchBooks(SearchBookRequest searchRequest) {
            log.debug("Searching books with query: {}", searchRequest.query());

            if (searchRequest.query() != null && !searchRequest.query().isEmpty()) {
                return bookMapper.toResponseList(bookRepository.searchBooks(searchRequest.query()));
            }

            return getAllBooks();
        }

        @Override
        public List<BookResponse> getBooksByAuthor(Long authorId) {
            log.debug("Fetching books by author ID: {}", authorId);
            if (!authorRepository.existsById(authorId)) {
                throw new AuthorNotFoundException(authorId);
            }
            return bookMapper.toResponseList(bookRepository.findByAuthorId(authorId));
        }

        @Override
        public List<BookResponse> getBooksByGenre(String genre) {
            log.debug("Fetching books by genre: {}", genre);
            return bookMapper.toResponseList(bookRepository.findByGenreContainingIgnoreCase(genre));
        }


        @Transactional
        public BookResponse updateBook(Long bookId, UpdateBookRequest request) {
            log.info("Updating book with ID: {}", bookId);
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException(bookId));

            // Validate authors exist if provided
            if (request.authorIds() != null && !request.authorIds().isEmpty()) {
                List<Author> authors = authorRepository.findAllById(request.authorIds());
                if (authors.size() != request.authorIds().size()) {
                    throw new AuthorNotFoundException("One or more authors not found");
                }
                book.setAuthors(authors);
            }

            bookMapper.updateEntity(request, book);
            Book updatedBook = bookRepository.save(book);

            log.info("Book updated successfully with ID: {}", bookId);
            return bookMapper.toResponse(updatedBook);
        }

        @Override
        @Transactional
        public BookResponse updateBookStatus(Long bookId, BookStatus newStatus) {
            log.info("Updating status for book ID: {} to {}", bookId, newStatus);
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException(bookId));

            book.setStatus(newStatus);
            Book updatedBook = bookRepository.save(book);

            log.info("Book status updated successfully for book ID: {}", bookId);
            return bookMapper.toResponse(updatedBook);
        }

    @Override
    @Transactional
    public BookResponse updateBookCopies(Long bookId, int newTotalCopies) {
        log.info("Updating copies for book ID: {} to {}", bookId, newTotalCopies);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        int currentAvailable = book.getAvailableCopies();
        int difference = newTotalCopies - book.getTotalCopies();

        book.setTotalCopies(newTotalCopies);
        book.setAvailableCopies(currentAvailable + difference);

        // Update status based on availability
        if (book.getAvailableCopies() > 0) {
            book.setStatus(BookStatus.AVAILABLE);
        } else {
            book.setStatus(BookStatus.BORROWED);
        }

        Book updatedBook = bookRepository.save(book);

        log.info("Book copies updated successfully for book ID: {}", bookId);
        return bookMapper.toResponse(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long bookId) {
        log.info("Deleting book with ID: {}", bookId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        // TODO: Add business rules (e.g., cannot delete book with active loans)
        bookRepository.delete(book);
        log.info("Book deleted successfully with ID: {}", bookId);
    }

    @Override
    public boolean bookExists(Long bookId) {
        return bookRepository.existsById(bookId);
    }

    @Override
    public boolean isIsbnAvailable(String isbn) {
        return !bookRepository.existsByIsbn(isbn);
    }

    @Override
    public int getAvailableCopies(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        return book.getAvailableCopies();
    }
}

