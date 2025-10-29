package com.JohnBravos.bookhub_manager.dto.Response;

import com.JohnBravos.bookhub_manager.core.enums.BookStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        String publisher,
        Integer publicationYear,
        String genre,
        String description,
        Integer totalCopies,
        Integer availableCopies,
        BookStatus status,
        List<AuthorResponse> authors,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
