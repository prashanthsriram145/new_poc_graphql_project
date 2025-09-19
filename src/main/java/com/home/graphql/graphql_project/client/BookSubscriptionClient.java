package com.home.graphql.graphql_project.client;

import com.home.graphql.graphql_project.entity.Book;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;

import java.net.URI;

public class BookSubscriptionClient {

    public static void main(String[] args) {
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

        // Execute subscription
        Flux<Book> bookFlux = graphQlClient.document(subscriptionQuery)
                .executeSubscription()
                .map(response -> response.field("bookAdded").toEntity(Book.class));

        // Listen for new books
        bookFlux.subscribe(
                book -> System.out.println("📖 New Book Added: " + book),
                error -> System.err.println("❌ Error: " + error),
                () -> System.out.println("✅ Subscription completed")
        );

        // Keep application running to listen
        try {
            Thread.sleep(600 * 1000); // 1 minutes
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}
