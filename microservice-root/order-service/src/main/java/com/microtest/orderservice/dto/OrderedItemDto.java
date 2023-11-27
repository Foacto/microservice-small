package com.microtest.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderedItemDto {
    @NotNull
    private Long id;
    @NotNull
    @NotBlank
    @Size(max = 50, message = "Code is too long")
    private String code;
    @NotNull
    private BigDecimal price;
    @NotNull
    private Integer quantity;
}
