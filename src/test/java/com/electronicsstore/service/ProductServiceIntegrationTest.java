package com.electronicsstore.service;

import com.electronicsstore.model.Discount;
import com.electronicsstore.model.Product;
import com.electronicsstore.repository.BasketItemRepository;
import com.electronicsstore.repository.DiscountRepository;
import com.electronicsstore.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ProductServiceIntegrationTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private BasketItemRepository basketItemRepository;

    @Autowired
    private ProductService productService;

    @Test
    void testCreateProduct() {
        // Given
        String name = "Test Product";
        BigDecimal price = new BigDecimal("99.99");
        int inventory = 100;

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

    @Test
    void testDeleteProduct() {
        // Given
        Discount discount = Discount.builder().threshold(1).amount(10.0).build();
        Product product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(99.99))
                .discount(discount)
                .inventory(10)
                .build();


        // When
        Discount savedDiscount = discountRepository.save(discount);
        Product savedProduct = productRepository.save(product);
        productService.removeProduct(savedProduct.getProductId());

        // Then
        assertThat(discountRepository.findById(savedDiscount.getDiscountId())).isEmpty();
        assertThat(productRepository.findById(savedProduct.getProductId())).isEmpty();


    }


}