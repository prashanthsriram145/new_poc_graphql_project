package com.home.graphql.graphql_project.service;

import com.home.graphql.graphql_project.entity.Author;
import com.home.graphql.graphql_project.repository.AuthorRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author findById(Long id) {
        return authorRepository.findById(id).isPresent() ? authorRepository.findById(id).get() : null;
    }

    public Author save(Author author) {
        return  authorRepository.save(author);
    }
}
