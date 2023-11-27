package com.microtest.orderservice.service;

import com.microtest.orderservice.dto.CheckStockRequest;
import com.microtest.orderservice.dto.InventoryResponse;
import com.microtest.orderservice.dto.OrderRequest;
import com.microtest.orderservice.dto.OrderedItemDto;
import com.microtest.orderservice.model.Order;
import com.microtest.orderservice.model.OrderedItem;
import com.microtest.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    public void createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        order.setOrderedItems(orderRequest.getOrderedItemDtos().stream().map(this::mappingToOrder).toList());

        List<CheckStockRequest> checkStockRequests = order.getOrderedItems().stream().map(
                orderedItem -> CheckStockRequest.builder()
                            .code(orderedItem.getCode())
                            .quantity(orderedItem.getQuantity())
                            .build()
        ).toList();

        //Check in stock
        InventoryResponse[] inventoryResponses = webClientBuilder.build().post()
                .uri("http://inventory-service/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(checkStockRequests), new ParameterizedTypeReference<List<CheckStockRequest>>() {})
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean isAllProductInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if(isAllProductInStock)
            orderRepository.save(order);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not in stock, please try again!");
    }

    private OrderedItem mappingToOrder(OrderedItemDto orderedItemDto) {
        OrderedItem item = new OrderedItem();
        item.setCode(orderedItemDto.getCode());
        item.setPrice(orderedItemDto.getPrice());
        item.setQuantity(orderedItemDto.getQuantity());
        return item;
    }
}
