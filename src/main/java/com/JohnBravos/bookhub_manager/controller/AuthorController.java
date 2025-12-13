package com.JohnBravos.bookhub_manager.controller;

import com.JohnBravos.bookhub_manager.dto.Request.CreateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ApiResponse;
import com.JohnBravos.bookhub_manager.dto.Response.AuthorResponse;
import com.JohnBravos.bookhub_manager.service.IAuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authors", description = "API for managing book authors")
public class AuthorController {

    private final IAuthorService authorService;

    @GetMapping
    @Operation(
            summary = "Get all authors",
            description = "Retrieve a paginated list of all authors in the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Authors retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Page<AuthorResponse>>> getAllAuthors(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort criteria (field,direction)", example = "id,asc")
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        Page<AuthorResponse> authorsPage = authorService.getAllAuthors(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(authorsPage));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get author by ID",
            description = "Retrieve a specific author by their unique identifier"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Author retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(
            @Parameter(description = "Author ID", example = "1")
            @PathVariable Long id
    ) {
        log.info("Fetching author with ID: {}", id);
        AuthorResponse author = authorService.getAuthorById(id);
        return ResponseEntity.ok(ApiResponse.success(author, "Author retrieved successfully"));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search authors by name",
            description = "Search for authors matching the given name pattern"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid search parameter"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> searchAuthors(
            @Parameter(description = "Author name to search for", example = "John")
            @RequestParam String name
    ) {
        log.info("Searching authors by name: {}", name);
        List<AuthorResponse> authors = authorService.searchAuthorsByName(name);
        return ResponseEntity.ok(ApiResponse.success(authors, "Authors search completed"));
    }

    @GetMapping("/with-books")
    @Operation(
            summary = "Get authors with books",
            description = "Retrieve all authors that have published books"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authors with books retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> getAuthorsWithBooks() {
        log.info("Fetching authors with books");
        List<AuthorResponse> authors = authorService.getAuthorsWithBooks();
        return ResponseEntity.ok(ApiResponse.success(authors, "Authors with books retrieved"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(
            summary = "Create a new author",
            description = "Create a new author (Librarian or Admin only)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Author created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
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
    @Operation(
            summary = "Update an author",
            description = "Update author details (Librarian or Admin only)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Author updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<ApiResponse<AuthorResponse>> updateAuthor(
            @Parameter(description = "Author ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateAuthorRequest request
    ) {
        log.info("Updating author with ID: {}", id);
        AuthorResponse author = authorService.updateAuthor(id, request);
        return ResponseEntity.ok(ApiResponse.success(author, "Author updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete an author",
            description = "Delete an author from the system (Admin only)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Author deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(
            @Parameter(description = "Author ID", example = "1")
            @PathVariable Long id
    ) {
        log.info("Deleting author with ID: {}", id);
        authorService.deleteAuthor(id);
        return ResponseEntity.ok(ApiResponse.success("Author deleted successfully"));
    }
}
