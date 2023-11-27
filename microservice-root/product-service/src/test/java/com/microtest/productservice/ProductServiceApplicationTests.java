package com.microtest.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microtest.productservice.dto.ProductRequest;
import com.microtest.productservice.model.Product;
import com.microtest.productservice.repository.ProductRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {
	@Container
	static final MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.32");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mySQLContainer::getUsername);
		registry.add("spring.datasource.password", mySQLContainer::getPassword);
	}

	@BeforeAll
	static void beforeAll() {
		mySQLContainer.start();
	}

	@AfterAll
	static void afterAll() {
		mySQLContainer.stop();
	}

	@Test
	void createProductTest() throws Exception {
		ProductRequest productRequest = createProductRequest();
		String content = objectMapper.writeValueAsString(productRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
				.andExpect(status().isCreated());

		List<Product> products = productRepository.findAll();
		Boolean flag = false;
		for (Product p: products) {
			if(p.getName().equals("Laptop Asus")) {
				flag = true;
				break;
			}
		}
		Assertions.assertEquals(true, flag);
	}

	private ProductRequest createProductRequest() {
		return ProductRequest.builder()
				.name("Laptop Asus")
				.des("Laptop")
				.price(BigDecimal.valueOf(15000000))
				.build();
	}

	private Product createProduct(String name) {
		return Product.builder()
				.name(name)
				.des("")
				.price(BigDecimal.valueOf((long)(Math.random() * 1000)))
				.build();
	}

	@Test
	void getProductsTest(){
		//Save products
		productRepository.save(createProduct("test 1"));
		productRepository.save(createProduct("test 2"));
		productRepository.save(createProduct("test 3"));

		//Get products
		List<Product> products = productRepository.findAll();
		Assertions.assertEquals(3, products.size());
		Boolean flag = false;
		for (Product p: products) {
			if(p.getName().equals("test 2")) {
				flag = true;
				break;
			}
		}
		Assertions.assertEquals(true, flag);
	}
}
