package com.microtest.inventoryservice.service;

import com.microtest.inventoryservice.dto.CheckStockRequest;
import com.microtest.inventoryservice.dto.InventoryResponse;
import com.microtest.inventoryservice.model.Inventory;
import com.microtest.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<CheckStockRequest> checkStockRequests) {
        return checkStockRequests.stream().map(
                checkStockRequest -> {
                    Optional<Inventory> inventory = inventoryRepository.findByCode(checkStockRequest.getCode());
                    if(inventory.isPresent())
                        return InventoryResponse.builder()
                                .code(inventory.get().getCode())
                                .isInStock(inventory.get().getQuantity() >= checkStockRequest.getQuantity())
                                .build();
                    else
                        return InventoryResponse.builder()
                                .code(checkStockRequest.getCode())
                                .isInStock(false)
                                .build();
                }
        ).toList();
    }
}
