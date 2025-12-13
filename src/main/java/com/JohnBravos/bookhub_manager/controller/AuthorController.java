package com.JohnBravos.bookhub_manager.controller;

import com.JohnBravos.bookhub_manager.dto.Request.CreateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ApiResponse;
import com.JohnBravos.bookhub_manager.dto.Response.AuthorResponse;
import com.JohnBravos.bookhub_manager.service.IAuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final IAuthorService authorService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuthorResponse>>> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        Page<AuthorResponse> authorsPage = authorService.getAllAuthors(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(authorsPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(
            @PathVariable Long id
    ) {
        log.info("Fetching author with ID: {}", id);
        AuthorResponse author = authorService.getAuthorById(id);
        return ResponseEntity.ok(ApiResponse.success(author, "Author retrieved successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> searchAuthors(
            @RequestParam String name
    ) {
        log.info("Searching authors by name: {}", name);
        List<AuthorResponse> authors = authorService.searchAuthorsByName(name);
        return ResponseEntity.ok(ApiResponse.success(authors, "Authors search completed"));
    }

    @GetMapping("/with-books")
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> getAuthorsWithBooks() {
        log.info("Fetching authors with books");
        List<AuthorResponse> authors = authorService.getAuthorsWithBooks();
        return ResponseEntity.ok(ApiResponse.success(authors, "Authors with books retrieved"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(
            @Valid @RequestBody CreateAuthorRequest request
    ) {
        log.info("Creating new author: {} {}", request.firstName(), request.lastName());
        AuthorResponse author = authorService.createAuthor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(author, "Author created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<AuthorResponse>> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAuthorRequest request
    ) {
        log.info("Updating author with ID: {}", id);
        AuthorResponse author = authorService.updateAuthor(id, request);
        return ResponseEntity.ok(ApiResponse.success(author, "Author updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(
            @PathVariable Long id
    ) {
        log.info("Deleting author with ID: {}", id);
        authorService.deleteAuthor(id);
        return ResponseEntity.ok(ApiResponse.success("Author deleted successfully"));
    }
}
