package com.JohnBravos.bookhub_manager.controller;

import com.JohnBravos.bookhub_manager.dto.Request.CreateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ApiResponse;
import com.JohnBravos.bookhub_manager.dto.Response.AuthorResponse;
import com.JohnBravos.bookhub_manager.service.IAuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "Authors", description = "Author Management API")
@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final IAuthorService authorService;

    @GetMapping
    @Operation(summary = "Get all authors", description = "Retrieve all authors with pagination support")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authors retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<AuthorResponse>>> getAllAuthors(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort format (e.g., id,asc)") @RequestParam(defaultValue = "id,asc") String sort) {
        Page<AuthorResponse> authorsPage = authorService.getAllAuthors(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(authorsPage));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID", description = "Retrieve a specific author by their ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Author retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(
            @Parameter(description = "Author ID") @PathVariable Long id) {
        log.info("Fetching author with ID: {}", id);
        AuthorResponse author = authorService.getAuthorById(id);
        return ResponseEntity.ok(ApiResponse.success(author, "Author retrieved successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search authors by name", description = "Search for authors by their name")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authors search completed")
    })
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> searchAuthors(
            @Parameter(description = "Author name to search") @RequestParam String name) {
        log.info("Searching authors by name: {}", name);
        List<AuthorResponse> authors = authorService.searchAuthorsByName(name);
        return ResponseEntity.ok(ApiResponse.success(authors, "Authors search completed"));
    }

    @GetMapping("/with-books")
    @Operation(summary = "Get authors with books", description = "Retrieve only authors that have associated books")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authors with books retrieved")
    })
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> getAuthorsWithBooks() {
        log.info("Fetching authors with books");
        List<AuthorResponse> authors = authorService.getAuthorsWithBooks();
        return ResponseEntity.ok(ApiResponse.success(authors, "Authors with books retrieved"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Create new author", description = "Create a new author (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Author created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(
            @Parameter(description = "Author creation request") @Valid @RequestBody CreateAuthorRequest request) {
        log.info("Creating new author: {} {}", request.firstName(), request.lastName());
        AuthorResponse author = authorService.createAuthor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(author, "Author created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Update author", description = "Update an existing author (LIBRARIAN or ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Author updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<AuthorResponse>> updateAuthor(
            @Parameter(description = "Author ID") @PathVariable Long id,
            @Parameter(description = "Author update request") @Valid @RequestBody UpdateAuthorRequest request) {
        log.info("Updating author with ID: {}", id);
        AuthorResponse author = authorService.updateAuthor(id, request);
        return ResponseEntity.ok(ApiResponse.success(author, "Author updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete author", description = "Delete an author (ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Author deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(
            @Parameter(description = "Author ID") @PathVariable Long id) {
        log.info("Deleting author with ID: {}", id);
        authorService.deleteAuthor(id);
        return ResponseEntity.ok(ApiResponse.success("Author deleted successfully"));
    }
}
