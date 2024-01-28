package com.electronicsstore.service;

import com.electronicsstore.model.BasketItem;
import com.electronicsstore.model.Customer;
import com.electronicsstore.model.Product;
import com.electronicsstore.repository.BasketItemRepository;
import com.electronicsstore.repository.CustomerRepository;
import com.electronicsstore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BasketItemServiceIntegrationTest {

    @Autowired
    private BasketItemService basketItemService;

    @Autowired
    private BasketItemRepository basketItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long customerId;
    private Long productId;

    @BeforeEach
    public void setUp() {
        Customer customer = Customer.builder().name("Test Customer").build();
        Customer savedCustomer = customerRepository.save(customer);
        customerId = savedCustomer.getCustomerId();

        Product product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.TEN)
                .inventory(10L)
                .build();
        Product savedProduct = productRepository.save(product);
        productId = savedProduct.getProductId();
    }

    @Test
    public void testGetBasketItemsByCustomerId() {
        setUpInitialBasketItem();

        List<BasketItem> basketItems = basketItemService.getBasketItemsByCustomerId(customerId);

        assertThat(basketItems).isNotEmpty();
        assertThat(basketItems).hasSize(1);
        assertThat(basketItems.getFirst().getCustomerId()).isEqualTo(customerId);
    }

    @Test
    public void testAddNewBasketItem() {
        BasketItem newBasketItem = basketItemService.addOrUpdate(customerId, productId, 2);

        assertThat(newBasketItem).isNotNull();
        assertThat(newBasketItem.getCustomerId()).isEqualTo(customerId);
        assertThat(newBasketItem.getProductId()).isEqualTo(productId);
        assertThat(newBasketItem.getQuantity()).isEqualTo(2);
    }

    @Test
    public void testUpdateExistingBasketItem() {
        setUpInitialBasketItem();

        BasketItem updatedBasketItem = basketItemService.addOrUpdate(customerId, productId, 3);

        assertThat(updatedBasketItem).isNotNull();
        assertThat(updatedBasketItem.getCustomerId()).isEqualTo(customerId);
        assertThat(updatedBasketItem.getProductId()).isEqualTo(productId);
        assertThat(updatedBasketItem.getQuantity()).isEqualTo(4);
    }

    @Test
    void testRemoveExistingBasketItem() {
        setUpInitialBasketItem();

        // When
        basketItemService.remove(customerId, productId);

        // Then
        Optional<BasketItem> deletedBasketItem = basketItemRepository.findByCustomerIdAndProductId(customerId, productId);
        assertThat(deletedBasketItem).isEmpty();
    }

    private void setUpInitialBasketItem(){
        // Setup initial basket item with quantity 1
        BasketItem existingBasketItem = BasketItem.builder()
                .customerId(customerId)
                .productId(productId)
                .quantity(1)
                .build();
        basketItemRepository.save(existingBasketItem);
    }
}