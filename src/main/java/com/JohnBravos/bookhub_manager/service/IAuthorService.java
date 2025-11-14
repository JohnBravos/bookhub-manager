package com.JohnBravos.bookhub_manager.service;

import com.JohnBravos.bookhub_manager.dto.Request.CreateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Response.AuthorResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IAuthorService {

    // CREATE
    AuthorResponse createAuthor(CreateAuthorRequest request);

    // READ
    AuthorResponse getAuthorById(Long id);
    List<AuthorResponse> getAllAuthors();
    Page<AuthorResponse> getAllAuthors(int page, int size, String sort);
    List<AuthorResponse> searchAuthorsByName(String name);
    List<AuthorResponse> getAuthorsWithBooks();

    // UPDATE
    AuthorResponse updateAuthor(Long authorId, UpdateAuthorRequest request);

    // DELETE
    void deleteAuthor(Long authorId);

    // UTILITY
    boolean authorExists(Long authorId);
    List<AuthorResponse> getAuthorsByNationality(String nationality);
}
