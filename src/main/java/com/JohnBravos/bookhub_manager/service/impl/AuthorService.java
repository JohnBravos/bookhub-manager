package com.JohnBravos.bookhub_manager.service.impl;

import com.JohnBravos.bookhub_manager.core.exceptions.custom.AuthorNotFoundException;
import com.JohnBravos.bookhub_manager.core.exceptions.custom.CannotDeleteException;
import com.JohnBravos.bookhub_manager.dto.Request.CreateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateAuthorRequest;
import com.JohnBravos.bookhub_manager.dto.Response.AuthorResponse;
import com.JohnBravos.bookhub_manager.mapper.AuthorMapper;
import com.JohnBravos.bookhub_manager.model.Author;
import com.JohnBravos.bookhub_manager.repository.AuthorRepository;
import com.JohnBravos.bookhub_manager.service.IAuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService implements IAuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    @Transactional
    public AuthorResponse createAuthor(CreateAuthorRequest request) {
        log.info("Creating new author: {} {}", request.firstName(), request.lastName());

        Author author = Author.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .nationality(request.nationality())
                .birthDate(request.birthDate())
                .biography(request.biography())
                .build();

        Author savedAuthor = authorRepository.save(author);
        log.info("Author created successfully with ID: {}", savedAuthor.getId());

        return authorMapper.toResponse(savedAuthor);
    }

    @Override
    public AuthorResponse getAuthorById(Long id) {
        log.debug("Fetching author by ID: {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
        return authorMapper.toResponse(author);
    }

    @Override
    public List<AuthorResponse> getAllAuthors() {
        log.debug("Fetching all authors");
        return authorMapper.toResponseList(authorRepository.findAll());
    }

    @Override
    public Page<AuthorResponse> getAllAuthors(int page, int size, String sort) {

        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction = Sort.Direction.ASC;

        if (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<Author> pageResult = authorRepository.findAll(pageable);

        return pageResult.map(authorMapper::toResponse);
    }

    @Override
    public List<AuthorResponse> searchAuthorsByName(String name) {
        log.debug("Searching authors by name: {}", name);
        return authorMapper.toResponseList(authorRepository.findByNameContaining(name));
    }

    @Override
    public List<AuthorResponse> getAuthorsWithBooks() {
        log.debug("Fetching authors with books");
        return authorMapper.toResponseList(authorRepository.findAuthorsWithBooks());
    }

    @Override
    public List<AuthorResponse> getAuthorsByNationality(String nationality) {
        log.debug("Fetching authors by nationality: {}", nationality);
        return authorMapper.toResponseList(authorRepository.findByNationality(nationality));
    }

    @Override
    @Transactional
    public AuthorResponse updateAuthor(Long authorId, UpdateAuthorRequest request) {
        log.info("Updating author with ID: {}", authorId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorId));

        authorMapper.updateEntity(request, author);
        Author updatedAuthor = authorRepository.save(author);

        log.info("Author updated successfully with ID: {}", authorId);
        return authorMapper.toResponse(updatedAuthor);
    }

    @Override
    @Transactional
    public void deleteAuthor(Long authorId) {
        log.info("Attempting to delete author with ID: {}", authorId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorId));

        validateAuthorCanBeDeleted(author);
        authorRepository.delete(author);
        log.info("Author deleted successfully with ID: {}", authorId);
    }

    private void validateAuthorCanBeDeleted(Author author) {
        boolean hasBooks = authorRepository.hasBooks(author.getId());
        if (hasBooks) {
            throw new CannotDeleteException("author", "Author has associated books");
        }
    }

    @Override
    public boolean authorExists(Long authorId) {
        return authorRepository.existsById(authorId);
    }
}
