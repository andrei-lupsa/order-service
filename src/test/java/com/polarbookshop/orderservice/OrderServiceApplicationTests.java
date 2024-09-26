package com.polarbookshop.orderservice;

import com.polarbookshop.orderservice.book.Book;
import com.polarbookshop.orderservice.book.BookClient;
import com.polarbookshop.orderservice.order.domain.Order;
import com.polarbookshop.orderservice.order.domain.OrderStatus;
import com.polarbookshop.orderservice.order.web.OrderRequest;
import com.polarbookshop.orderservice.testcontainers.TestContainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@Testcontainers
@ImportTestcontainers(TestContainers.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookClient bookClient;

    @Test
    void whenGetOrdersThenReturn() {
        String bookIsbn = "1234567893";
        Book book = new Book(bookIsbn, "Title", "Author", 9.90);
        given(bookClient.getBookByIsbn(bookIsbn)).willReturn(Mono.just(book));
        OrderRequest orderRequest = new OrderRequest(bookIsbn, 1);
        Order expectedOrder = webTestClient.post().uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).returnResult().getResponseBody();
        assertThat(expectedOrder).isNotNull();

        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Order.class).value(orders -> {
                    assertThat(orders.stream().filter(order -> order.bookIsbn().equals(bookIsbn)).findAny()).isNotEmpty();
                });
    }

    @Test
    void whenPostRequestAndBookExistsThenOrderAccepted() {
        String bookIsbn = "1234567899";
        Book book = new Book(bookIsbn, "Title", "Author", 9.90);
        given(bookClient.getBookByIsbn(bookIsbn)).willReturn(Mono.just(book));
        OrderRequest orderRequest = new OrderRequest(bookIsbn, 3);

        Order createdOrder = webTestClient.post().uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).returnResult().getResponseBody();

        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.bookIsbn()).isEqualTo(orderRequest.isbn());
        assertThat(createdOrder.quantity()).isEqualTo(orderRequest.quantity());
        assertThat(createdOrder.bookName()).isEqualTo(book.title() + " - " + book.author());
        assertThat(createdOrder.bookPrice()).isEqualTo(book.price());
        assertThat(createdOrder.status()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void whenPostRequestAndBookNotExistsThenOrderRejected() {
        String bookIsbn = "1234567894";
        given(bookClient.getBookByIsbn(bookIsbn)).willReturn(Mono.empty());
        OrderRequest orderRequest = new OrderRequest(bookIsbn, 3);

        Order createdOrder = webTestClient.post().uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).returnResult().getResponseBody();

        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.bookIsbn()).isEqualTo(orderRequest.isbn());
        assertThat(createdOrder.quantity()).isEqualTo(orderRequest.quantity());
        assertThat(createdOrder.status()).isEqualTo(OrderStatus.REJECTED);
    }

}