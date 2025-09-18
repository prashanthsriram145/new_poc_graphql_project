package com.home.graphql.graphql_project.controller;

import com.home.graphql.graphql_project.entity.Book;
import com.home.graphql.graphql_project.repository.BookRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class BookController {
    private final BookRepository repository;

    public BookController(BookRepository repository) {
        this.repository = repository;
    }

    @QueryMapping
    public List<Book> getBooks(@Argument Integer limit, @Argument Integer offset) {
        Pageable pageable = PageRequest.of(
                offset != null ? offset : 0,
                limit != null ? limit : 10
        );
        return repository.findAll(pageable).getContent();
    }

    @QueryMapping
    public List<Book> searchBooks(@Argument String author, @Argument String titleContains) {
        if (author != null && !author.isEmpty()) {
            return repository.findByAuthorContainingIgnoreCase(author);
        } else if (titleContains != null && !titleContains.isEmpty()) {
            return repository.findByTitleContainingIgnoreCase(titleContains);
        }
        return repository.findAll();
    }


    @QueryMapping
    public Book getBookById(@Argument Long id) {
        return repository.findById(id).orElse(null);
    }

    @MutationMapping
    public Book createBook(@Argument String title, @Argument String author) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        return repository.save(book);
    }

    @MutationMapping
    public Book updateBook(@Argument Long id, @Argument String title, @Argument String author) {
        return repository.findById(id).map(existing -> {
            if (title != null) existing.setTitle(title);
            if (author != null) existing.setAuthor(author);
            return repository.save(existing);
        }).orElse(null);
    }

    @MutationMapping
    public Boolean deleteBook(@Argument Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
