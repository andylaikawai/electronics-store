package com.electronicsstore.service;

import com.electronicsstore.model.*;
import com.electronicsstore.repository.*;
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

    @Autowired
    private DiscountRepository discountRepository;

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
                .inventory(99)
                .build();
        product = productRepository.save(product);
        productId = product.getProductId();

        BasketItem basketItem = BasketItem.builder()
                .customerId(customerId)
                .productId(productId)
                .quantity(2)
                .build();
        basketItemRepository.save(basketItem);
    }

    @Test
    public void testPreviewReceipt() {
        // When
        Receipt preview = checkoutService.previewReceipt(customerId);

        // Then
        assertThat(preview.getTotalPrice()).isEqualTo(BigDecimal.valueOf(199.98));
        assertThat(preview.getReceiptItems()).hasSize(1);
        assertThat(preview.getReceiptItems().getFirst().getProductName()).isEqualTo("Test Product");
        assertThat(preview.getReceiptItems().getFirst().getQuantity()).isEqualTo(2);
        assertThat(preview.getReceiptItems().getFirst().getReceiptItemId()).isNull();
    }

    @Test
    public void testCheckoutWithoutDiscount() {
        // When
        Receipt receipt = checkoutService.checkout(customerId);

        // Then
        assertThat(receipt).isNotNull();
        assertThat(receipt.getReceiptId()).isNotNull();
        assertThat(receipt.getCustomer()).isNotNull();
        assertThat(receipt.getCustomer().getCustomerId()).isEqualTo(customerId);
        assertThat(receipt.getTotalPrice()).isEqualTo(BigDecimal.valueOf(199.98));
        assertThat(receipt.getReceiptItems()).hasSize(1);
        assertThat(receipt.getReceiptItems().getFirst().getProductName()).isEqualTo("Test Product");
        assertThat(receipt.getReceiptItems().getFirst().getQuantity()).isEqualTo(2);

        // Verify that product inventory is updated
        Product productPostCheckout = productRepository.findById(productId).orElseThrow();
        assertThat(productPostCheckout.getInventory()).isEqualTo(97);

        // Verify that the basket is now empty
        List<BasketItem> basketItemsPostCheckout = basketItemRepository.findByCustomerId(customerId);
        assertThat(basketItemsPostCheckout).isEmpty();
    }

    @Test
    public void testCheckoutWithDiscount() {
        // Buy 1 get 50% off the second
        Discount discount = Discount.builder()
                .threshold(2)
                .amount(0.75)
                .build();
        Discount savedDiscount = discountRepository.save(discount);

        Product product = productRepository.findById(productId).orElseThrow();
        product.setDiscount(savedDiscount);

        productRepository.save(product);


        // Test if we buy 3 items
        BasketItem item = basketItemRepository.findByCustomerIdAndProductId(customerId, productId).orElseThrow();
        item.setQuantity(3);
        basketItemRepository.save(item);

        // When
        Receipt receipt = checkoutService.checkout(customerId);

        // Then
        assertThat(receipt).isNotNull();
        assertThat(receipt.getReceiptId()).isNotNull();
        assertThat(receipt.getCustomer()).isNotNull();
        assertThat(receipt.getCustomer().getCustomerId()).isEqualTo(customerId);
        assertThat(receipt.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(249.975));
        assertThat(receipt.getReceiptItems()).hasSize(1);
        assertThat(receipt.getReceiptItems().getFirst().getProductName()).isEqualTo("Test Product");
        assertThat(receipt.getReceiptItems().getFirst().getQuantity()).isEqualTo(3);

        // Verify that product inventory is updated
        Product productPostCheckout = productRepository.findById(productId).orElseThrow();
        assertThat(productPostCheckout.getInventory()).isEqualTo(96);

        // Verify that the basket is now empty
        List<BasketItem> basketItemsPostCheckout = basketItemRepository.findByCustomerId(customerId);
        assertThat(basketItemsPostCheckout).isEmpty();
    }

    @Test
    public void testProductUpdatedAfterCheckout() {
        // When
        Receipt receipt = checkoutService.checkout(customerId);

        // Then
        assertThat(receipt).isNotNull();
        assertThat(receipt.getReceiptId()).isNotNull();
        assertThat(receipt.getCustomer()).isNotNull();
        assertThat(receipt.getCustomer().getCustomerId()).isEqualTo(customerId);
        assertThat(receipt.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(199.98));
        assertThat(receipt.getReceiptItems()).hasSize(1);
        assertThat(receipt.getReceiptItems().getFirst().getProductName()).isEqualTo("Test Product");
        assertThat(receipt.getReceiptItems().getFirst().getQuantity()).isEqualTo(2);

        // When we update the product now
        Product productPostCheckout = productRepository.findById(productId).orElseThrow();
        productPostCheckout.setPrice(BigDecimal.ONE);

        // Verify that the receipt is not mutated
        Receipt receiptAfterProductUpdate = receiptRepository.findById(receipt.getReceiptId()).orElseThrow();
        assertThat(receiptAfterProductUpdate.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(199.98));
    }

    @Test
    public void testCheckoutWithUnknownCustomer() {
        Long invalidCustomerId = 999L;

        // When/Then
        assertThrows(RuntimeException.class, () -> checkoutService.checkout(invalidCustomerId));
    }

}