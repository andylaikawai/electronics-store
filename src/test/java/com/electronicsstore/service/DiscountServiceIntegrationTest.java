package com.electronicsstore.service;

import com.electronicsstore.model.Product;
import com.electronicsstore.model.Discount;
import com.electronicsstore.repository.DiscountRepository;
import com.electronicsstore.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(DiscountService.class)
class DiscountServiceIntegrationTest {

    @Autowired
    private DiscountService discountService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Test
    @Transactional
    void testAddDiscountToProduct() {
        // When
        Product product = Product.builder()
                .name("Integration Test Product")
                .price(BigDecimal.valueOf(200.0))
                .inventory(5)
                .build();
        product = productRepository.save(product);

        Discount discount = Discount.builder()
                .amount(25.0)
                .threshold(2)
                .build();
        discount = discountRepository.save(discount);

        // Then
        Product updatedProduct = discountService.addDiscountToProduct(product.getProductId(), discount);

        assertNotNull(updatedProduct.getDiscount());
        assertThat(discount.getDiscountId()).isEqualTo(updatedProduct.getDiscount().getDiscountId());
    }

    @Test
    @Transactional
    void testRemoveDiscountFromProduct() {
        // When
        Discount discount = Discount.builder()
                .amount(25.0)
                .threshold(2)
                .build();
        discount = discountRepository.save(discount);

        Product product = Product.builder()
                .name("Integration Test Product")
                .price(BigDecimal.valueOf(200.0))
                .discount(discount)
                .inventory(5)
                .build();
        productRepository.save(product);

        discountService.remove(discount.getDiscountId());

        // Then
        Optional<Discount> deletedDiscount = discountRepository.findById(discount.getDiscountId());
        assertThat(deletedDiscount).isEmpty();
    }
}