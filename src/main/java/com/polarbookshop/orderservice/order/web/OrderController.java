package com.polarbookshop.orderservice.order.web;

import com.polarbookshop.orderservice.order.domain.Order;
import com.polarbookshop.orderservice.order.domain.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public Flux<Order> getAllOrders(@AuthenticationPrincipal final Jwt jwt) {
        log.info("Fetching all orders");
        return orderService.getAllOrders(jwt.getSubject());
    }

    @PostMapping
    public Mono<Order> submitOrder(@RequestBody @Valid OrderRequest orderRequest) {
        log.info("Order for {} copies of the book with ISBN {}", orderRequest.quantity(), orderRequest.isbn());
        return orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity());
    }
}