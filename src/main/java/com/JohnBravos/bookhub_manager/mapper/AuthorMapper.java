package com.JohnBravos.bookhub_manager.mapper;

import com.JohnBravos.bookhub_manager.dto.Request.CreateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Response.AuthorResponse;
import com.JohnBravos.bookhub_manager.model.Author;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorMapper {

    public Author toEntity(CreateAuthorRequest request) {
        return Author.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .nationality(request.nationality())
                .birthDate(request.birthDate())
                .biography(request.biography())
                .build();
    }

    /**
     * Μετάφραση από Author -> AuthorResponse
     */
    public AuthorResponse toResponse(Author author) {
        if (author == null) {
            return null;
        }

        return new AuthorResponse(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getNationality(),
                author.getFullName(),
                author.getBiography(),
                author.getCreatedAt(),
                author.getUpdatedAt()
        );
    }

    /**
     * Μετατροπή λίστας Authors -> AuthorResponses
     */
    public List<AuthorResponse> toResponseList(List<Author> authors) {
        if (authors == null) {
            return List.of();
        }

        return authors.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
