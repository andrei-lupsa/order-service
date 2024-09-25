package com.polarbookshop.orderservice.book;

import com.polarbookshop.orderservice.config.ClientProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@RequiredArgsConstructor
@Component
public class BookClient {
    private static final String BOOKS_ROOT_API = "/books/{isbn}";
    private final ClientProperties clientProperties;
    private final WebClient webClient;

    public Mono<Book> getBookByIsbn(String isbn) {
        return webClient
                .get()
                .uri(BOOKS_ROOT_API, isbn)
                .retrieve()
                .bodyToMono(Book.class)
                .timeout(clientProperties.timeout(), Mono.empty())
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.empty())
                .retryWhen(
                        Retry.backoff(clientProperties.retryBackoffMaxAttempts(), clientProperties.retryMinBackoff())
                )
                .onErrorResume(Exception.class, ex -> Mono.empty());
    }
}