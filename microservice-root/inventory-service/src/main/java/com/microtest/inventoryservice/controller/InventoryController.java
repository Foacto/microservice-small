package com.microtest.inventoryservice.controller;

import com.microtest.inventoryservice.dto.CheckStockRequest;
import com.microtest.inventoryservice.dto.InventoryResponse;
import com.microtest.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestBody List<CheckStockRequest> checkStockRequests) {
        return inventoryService.isInStock((checkStockRequests));
    }
}
