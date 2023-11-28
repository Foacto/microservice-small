package com.microtest.orderservice.controller;

import com.microtest.orderservice.dto.AppResponse;
import com.microtest.orderservice.dto.OrderRequest;
import com.microtest.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<ResponseEntity<AppResponse>> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return CompletableFuture.supplyAsync(() -> orderService.createOrder(orderRequest));
    }

    public CompletableFuture<ResponseEntity<AppResponse>> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(
                AppResponse.builder().message("Something went wrong, please order later!").build(),
                HttpStatus.REQUEST_TIMEOUT));
    }
}
