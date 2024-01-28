package com.electronicsstore.service;

import com.electronicsstore.model.Product;
import com.electronicsstore.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ProductServiceTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Test
    void testCreateProduct() {
        // Given
        String name = "Test Product";
        BigDecimal price = new BigDecimal("99.99");
        Long inventory = 100L;

        // When
        Product savedProduct = productService.createProduct(name, price, inventory);


        // Then
        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getProductId());
        assertEquals(name, savedProduct.getName());
        assertEquals(price, savedProduct.getPrice());
        assertEquals(inventory, savedProduct.getInventory());

        Product fetchedProduct = productRepository.findById(savedProduct.getProductId()).orElse(null);
        assertNotNull(fetchedProduct);
        assertEquals(savedProduct, fetchedProduct);

    }
}