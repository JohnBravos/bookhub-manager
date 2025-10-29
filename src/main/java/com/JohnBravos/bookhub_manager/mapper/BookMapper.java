package com.JohnBravos.bookhub_manager.mapper;

import com.JohnBravos.bookhub_manager.dto.Request.CreateBookRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateBookRequest;
import com.JohnBravos.bookhub_manager.dto.Response.BookResponse;
import com.JohnBravos.bookhub_manager.model.Book;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    private final AuthorMapper authorMapper;

    public BookMapper(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    /**
     * Μετατροπή από CreateBookRequest -> Book
     */
    public Book toEntity(CreateBookRequest request) {
        return Book.builder()
                .title(request.title())
                .isbn(request.isbn())
                .publisher(request.publisher())
                .publicationYear(request.publicationYear())
                .publicationYear(request.publicationYear())
                .genre(request.genre())
                .description(request.description())
                .totalCopies(request.totalCopies())
                .build();
    }

    /**
     * Μετατροπή από Book -> BookResponse
     */
    public BookResponse toResponse(Book book) {
        if (book == null) {
            return null;
        }

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublisher(),
                book.getPublicationYear(),
                book.getGenre(),
                book.getDescription(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getStatus(),
                book.getAuthors() != null ? authorMapper.toResponseList(book.getAuthors())
                        : List.of(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }

    /**
     * Ενημέρωση Book από UpdateBookRequest
     */
    public void updateEntity(UpdateBookRequest request, Book book) {
        if (request.title() != null) {
            book.setTitle(request.title());
        }
        if (request.publisher() != null) {
            book.setPublisher(request.publisher());
        }
        if (request.publicationYear() != null) {
            book.setPublicationYear(request.publicationYear());
        }
        if (request.genre() != null) {
            book.setGenre(request.genre());
        }
        if (request.description() != null) {
            book.setDescription(request.description());
        }
        if (request.totalCopies() != null) {
            book.setTotalCopies(request.totalCopies());
        }
    }

    /**
     * Μετατροπή λίστας Books -> BookResponses
     */
    public List<BookResponse> toResponseList(List<Book> books) {
        if (books == null) {
            return List.of();
        }
        return books.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
