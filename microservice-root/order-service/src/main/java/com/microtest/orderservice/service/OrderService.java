package com.microtest.orderservice.service;

import com.microtest.orderservice.dto.*;
import com.microtest.orderservice.model.Order;
import com.microtest.orderservice.model.OrderedItem;
import com.microtest.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    public ResponseEntity<AppResponse> createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        order.setOrderedItems(orderRequest.getOrderedItemDtos().stream().map(this::mappingToOrder).toList());

        List<CheckStockRequest> checkStockRequests = order.getOrderedItems().stream().map(
                orderedItem -> CheckStockRequest.builder()
                            .code(orderedItem.getCode())
                            .quantity(orderedItem.getQuantity())
                            .build()
        ).toList();

        //Create span to visualize request flow
        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");
        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {
            //Check in stock
            InventoryResponse[] inventoryResponses = webClientBuilder.build().post()
                    .uri("http://inventory-service/api/inventory")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(checkStockRequests), new ParameterizedTypeReference<List<CheckStockRequest>>() {})
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean isAllProductInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

            if(isAllProductInStock){
                orderRepository.save(order);
                return new ResponseEntity<>(AppResponse.builder()
                        .message("Create new order successfully!").build(),
                        HttpStatus.CREATED);
            }
            else
                return new ResponseEntity<>(AppResponse.builder()
                        .message("Product is not in stock, please try again!").build(),
                        HttpStatus.BAD_REQUEST);
        } finally {
            inventoryServiceLookup.end();
        }


    }

    private OrderedItem mappingToOrder(OrderedItemDto orderedItemDto) {
        OrderedItem item = new OrderedItem();
        item.setCode(orderedItemDto.getCode());
        item.setPrice(orderedItemDto.getPrice());
        item.setQuantity(orderedItemDto.getQuantity());
        return item;
    }
}
