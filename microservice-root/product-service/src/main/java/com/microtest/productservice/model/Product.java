package com.microtest.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tblproduct")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(generator = "product_id_generator")
    @GenericGenerator(name = "product_id_generator", strategy = "com.microtest.productservice.generator.ProductIdGenerator")
    private String id;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = true, length = 256)
    private String des;
    @Column(nullable = false)
    private BigDecimal price;
}
