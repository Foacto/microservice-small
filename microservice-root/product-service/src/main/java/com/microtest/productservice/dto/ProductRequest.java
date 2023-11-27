package com.microtest.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotNull(message = "Name ís mandatory")
    @NotBlank(message = "Name ís mandatory")
    @Size(max = 50, message = "Name is too long")
    private String name;
    @Size(max = 256, message = "Description is too long")
    private String des;
    @Digits(integer = 10, fraction = 3, message = "Price is not valid")
    @NotNull(message = "Price ís mandatory")
    private BigDecimal price;
}
