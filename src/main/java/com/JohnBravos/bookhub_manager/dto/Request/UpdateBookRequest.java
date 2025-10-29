package com.JohnBravos.bookhub_manager.dto.Request;

import jakarta.validation.constraints.*;

import java.util.List;

public record UpdateBookRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 500, message = "Title must not exceed 500 characters")
        String title,

        @NotBlank(message = "Publisher is required")
        @Size(max = 255, message = "Publisher must not exceed 255 characters")
        String publisher,

        @NotNull(message = "Publication year is required")
        @Min(value = 1000, message = "Publication year must be after 1000")
        @Max(value = 2030, message = "Publication year must be before 2030")
        Integer publicationYear,

        @NotBlank(message = "Genre is required")
        @Size(max = 100, message = "Genre must not exceed 100 characters")
        String genre,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @NotNull(message = "Total copies is required")
        @Min(value = 1, message = "Total copies must be at least 1")
        Integer totalCopies,

        @NotNull(message = "At least one author is required")
        @Size(min = 1, message = "At least one author is required")
        List<Long> authorIds
) {}
