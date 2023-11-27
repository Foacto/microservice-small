package com.microtest.orderservice.controller;

import com.microtest.orderservice.dto.AppResponse;
import com.microtest.orderservice.dto.OrderRequest;
import com.microtest.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AppResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        orderService.createOrder(orderRequest);
        return new ResponseEntity<>(
                AppResponse.builder().message("Create new order successfully!").build(),
                HttpStatus.CREATED);
    }
}
