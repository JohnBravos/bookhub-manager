package com.JohnBravos.bookhub_manager.dto.Response;

public record SearchBookRequest(
        String query,
        String genre,
        String author,
        Integer publicationYearFrom,
        Integer publicationYearTo,
        Boolean availableOnly,
        String sortBy,
        String sortDirection
) {}
