package com.microtest.inventoryservice.dto;

import lombok.Data;

@Data
public class CheckStockRequest {
    private String code;
    private Integer quantity;
}
