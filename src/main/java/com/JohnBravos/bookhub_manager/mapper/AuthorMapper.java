package com.JohnBravos.bookhub_manager.mapper;

import com.JohnBravos.bookhub_manager.dto.Request.CreateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Response.AuthorResponse;
import com.JohnBravos.bookhub_manager.model.Author;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorMapper {

    /**
     * Ενημέρωση Author από UpdateAuthorRequest
     */
    public void updateEntity(UpdateAuthorRequest request, Author author) {
        if (request.firstName() != null) {
            author.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            author.setLastName(request.lastName());
        }
        if (request.nationality() != null) {
            author.setNationality(request.nationality());
        }
        if (request.birthDate() != null) {
            author.setBirthDate(request.birthDate());
        }
        if (request.biography() != null) {
            author.setBiography(request.biography());
        }
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
        if (authors == null || authors.isEmpty()) {
            return List.of();
        }

        return authors.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
