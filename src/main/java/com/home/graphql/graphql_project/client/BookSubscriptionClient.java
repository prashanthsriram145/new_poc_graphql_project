package com.home.graphql.graphql_project.client;

import com.home.graphql.graphql_project.entity.Book;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class BookSubscriptionClient {

    public static void main(String[] args) throws InterruptedException {
        // Create WebSocket client
        ReactorNettyWebSocketClient webSocketClient = new ReactorNettyWebSocketClient();

        WebSocketGraphQlClient graphQlClient = WebSocketGraphQlClient.builder(
                URI.create("ws://localhost:8080/graphql"), webSocketClient
        ).build();

        // GraphQL subscription query
        String subscriptionQuery = """
            subscription {
                bookAdded {
                    id
                    title
                    author {
                    id
                    name
                    }
                }
            }
        """;


        // execute subscription and safely flatten possible reactive entity
        Flux<Book> bookFlux = graphQlClient.document(subscriptionQuery)
                .executeSubscription()
                .flatMap(response -> {
                    try {
                        Object entity = response.field("bookAdded").toEntity(Book.class);
                        // handle both synchronous Book and Mono<Book>
                        if (entity instanceof Mono) {
                            @SuppressWarnings("unchecked")
                            Mono<Book> monoBook = (Mono<Book>) entity;
                            return monoBook.flux();
                        } else if (entity instanceof Book) {
                            return Flux.just((Book) entity);
                        } else {
                            return Flux.empty();
                        }
                    } catch (Throwable t) {
                        return Flux.error(t);
                    }
                });

        CountDownLatch latch = new CountDownLatch(1);

        bookFlux
                .doOnSubscribe(s -> System.out.println("→ Subscription started"))
                .doOnNext(book -> System.out.println("📖 New Book Added: " + book))
                .doOnError(err -> {
                    System.err.println("❌ Error: " + err);
                    latch.countDown();
                })
                .doOnComplete(() -> {
                    System.out.println("✅ Subscription completed");
//                    latch.countDown();
                })
                .subscribe();

        // keep application alive until completion/error
        latch.await();
    }


}
