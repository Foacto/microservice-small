package com.microtest.productservice.service;

import com.microtest.productservice.dto.ProductRequest;
import com.microtest.productservice.dto.AppResponse;
import com.microtest.productservice.dto.ProductResponse;
import com.microtest.productservice.model.Product;
import com.microtest.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public AppResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .des(productRequest.getDes())
                .price(productRequest.getPrice())
                .build();

        // Save product to database
        productRepository.save(product);
        log.info("Product ID:{} saved", product.getId());

        return AppResponse.builder()
                .message("Product create successful")
                .build();
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        //Mapping data to response
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .des(product.getDes())
                .price(product.getPrice())
                .build();
    }
}
