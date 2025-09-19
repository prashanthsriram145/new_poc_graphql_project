package com.home.graphql.graphql_project.controller;

import com.home.graphql.graphql_project.entity.Author;
import com.home.graphql.graphql_project.entity.Book;
import com.home.graphql.graphql_project.repository.AuthorRepository;
import com.home.graphql.graphql_project.repository.BookRepository;
import com.home.graphql.graphql_project.service.AuthorService;
import com.home.graphql.graphql_project.service.BookService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;

@Controller
public class BookController {
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final BookService bookService;

    public BookController(BookRepository bookRepository, AuthorRepository authorRepository, AuthorService authorService, BookService bookService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @QueryMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @QueryMapping
    public List<Book> getBooks(@Argument Integer limit, @Argument Integer offset) {
        Pageable pageable = PageRequest.of(
                offset != null ? offset : 0,
                limit != null ? limit : 10
        );
        return bookRepository.findAll(pageable).getContent();
    }

    @QueryMapping
    public List<Book> searchBooks(@Argument String author, @Argument String titleContains) {
        if (titleContains != null && !titleContains.isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCase(titleContains);
        }
        return bookRepository.findAll();
    }


    @QueryMapping
    public Book getBookById(@Argument Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @MutationMapping
    public Book createBook(@Argument String title, @Argument String authorName) {
        Author author = new Author();
        author.setName(authorName);
        author = authorService.save(author);
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book = bookService.addBook(book);
        return book;
    }

    @MutationMapping
    public Book updateBook(@Argument Long id, @Argument String title, @Argument String authorName) {
        return bookRepository.findById(id).map(existing -> {
            if (title != null) existing.setTitle(title);
            if (authorName != null) {
                Author author = authorService.findById(existing.getAuthor().getId());
                author.setName(authorName);
                authorService.save(author);
            }
            return bookRepository.save(existing);
        }).orElse(null);
    }

    @MutationMapping
    public Boolean deleteBook(@Argument Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Resolving nested field: Book.author
    @SchemaMapping
    public Author author(Book book) {
        return authorService.findById(book.getAuthor().getId());
    }

    @SubscriptionMapping
    public Flux<Book> bookAdded() {
        return bookService.getBookStream(); // Emits new books reactively
    }
}
