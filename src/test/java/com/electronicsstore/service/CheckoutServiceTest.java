package com.electronicsstore.service;

import com.electronicsstore.model.BasketItem;
import com.electronicsstore.model.Customer;
import com.electronicsstore.model.Product;
import com.electronicsstore.model.Receipt;
import com.electronicsstore.repository.BasketItemRepository;
import com.electronicsstore.repository.CustomerRepository;
import com.electronicsstore.repository.ProductRepository;
import com.electronicsstore.repository.ReceiptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CheckoutServiceIntegrationTest {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private BasketItemRepository basketItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    private Long customerId;
    private Long productId;

    @BeforeEach
    public void setUp() {
        Customer customer = Customer.builder()
                .name("Test Customer")
                .build();
        customer = customerRepository.save(customer);
        customerId = customer.getCustomerId();

        Product product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(99.99))
                .inventory(99L)
                .build();
        product = productRepository.save(product);
        productId = product.getProductId();

        BasketItem basketItem = BasketItem.builder()
                .quantity(2)
                .customerId(customerId)
                .productId(productId)
                .build();
        basketItemRepository.save(basketItem);
    }

    @Test
    public void testPreviewReceipt() {
        // Given the customer has basket items

        // When
        Receipt preview = checkoutService.previewReceipt(customerId);

        // Then
        assertThat(preview.getTotalPrice()).isEqualTo(BigDecimal.valueOf(199.98));
        assertThat(preview.getReceiptItems()).hasSize(1);
        assertThat(preview.getReceiptItems().getFirst().getProduct().getProductId()).isEqualTo(productId);
        assertThat(preview.getReceiptItems().getFirst().getQuantity()).isEqualTo(2);
    }

    @Test
    public void testCheckout() {
        // Given the customer has basket items

        // When
        Receipt receipt = checkoutService.checkout(customerId);

        // Then
        assertThat(receipt).isNotNull();
        assertThat(receipt.getReceiptId()).isNotNull(); // Saved receipts should have an ID
        assertThat(receipt.getTotalPrice()).isEqualTo(BigDecimal.valueOf(199.98));
        assertThat(receipt.getReceiptItems()).hasSize(1);
        assertThat(receipt.getReceiptItems().getFirst().getProduct().getProductId()).isEqualTo(productId);
        assertThat(receipt.getReceiptItems().getFirst().getQuantity()).isEqualTo(2);

        // Verify that the basket is now empty
        List<BasketItem> basketItemsPostCheckout = basketItemRepository.findByCustomerId(customerId);
        assertThat(basketItemsPostCheckout).isEmpty();
    }

    @Test
    public void whenCheckoutWithNoCustomer_thenExceptionThrown() {
        Long invalidCustomerId = 999L;

        // When/Then
        assertThrows(RuntimeException.class, () -> checkoutService.checkout(invalidCustomerId));
    }

}