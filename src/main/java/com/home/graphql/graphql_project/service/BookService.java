package com.home.graphql.graphql_project.service;

import com.home.graphql.graphql_project.entity.Book;
import com.home.graphql.graphql_project.repository.BookRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Data
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Reactive sink to emit events
    private final Sinks.Many<Book> bookSink = Sinks.many().multicast().onBackpressureBuffer();

    public Book addBook(Book book) {
        bookRepository.save(book);
        bookSink.tryEmitNext(book); // emit new book to subscribers
        return book;
    }

    // GraphQL subscription will consume this
    public Flux<Book> getBookStream() {
        return bookSink.asFlux();
    }
}
