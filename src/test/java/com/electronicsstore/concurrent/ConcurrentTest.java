package com.electronicsstore.concurrent;

import com.electronicsstore.model.BasketItem;
import com.electronicsstore.model.Customer;
import com.electronicsstore.model.Receipt;
import com.electronicsstore.service.BasketItemService;
import com.electronicsstore.service.CheckoutService;
import com.electronicsstore.service.CustomerService;
import com.electronicsstore.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ConcurrentTest {

    @Autowired
    private BasketItemService basketItemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CheckoutService checkoutService;

    private final int numberOfConcurrentCustomers = 10;

    private final ExecutorService executorService = Executors.newFixedThreadPool(numberOfConcurrentCustomers);

    private final List<Customer> customers = new ArrayList<>();

    private Long productIdToAdd;

    private final BigDecimal productUnitPrice = BigDecimal.valueOf(9.99);

    @BeforeEach
    public void setUp() {
        // Initialize customers and product
        productIdToAdd = productService.createProduct("MOCK_PRODUCT", productUnitPrice, 20).getProductId();
        for (int i = 0; i < numberOfConcurrentCustomers; i++) {
            Customer customer = customerService.createCustomer("MOCK-CUSTOMER-" + i);
            customers.add(customer);
        }
    }

    @Test
    public void testConcurrentBasketOperations() throws InterruptedException {

        int timeoutInMinutes = 1;
        int quantityOfProductToAdd = 2;

        CountDownLatch addBasketItemsLatch = addItemsToBasketConcurrently(quantityOfProductToAdd);
        boolean finished = addBasketItemsLatch.await(timeoutInMinutes, TimeUnit.MINUTES);
        assertTrue(finished, "Not all operations finished in the expected timeframe");

        // check the state of each basket after adding items
        for (Customer customer : customers) {
            List<BasketItem> addedItem = basketItemService.getBasketItemsByCustomerId(customer.getCustomerId());
            assertEquals(1, addedItem.size());
            assertEquals(productIdToAdd, addedItem.getFirst().getProductId());
            assertEquals(quantityOfProductToAdd, addedItem.getFirst().getQuantity());
        }


        CountDownLatch checkoutLatch = checkoutConcurrently();
        finished = checkoutLatch.await(timeoutInMinutes, TimeUnit.MINUTES);
        assertTrue(finished, "Not all checkout operations finished in the expected timeframe");

        executorService.shutdown();
    }

    private CountDownLatch addItemsToBasketConcurrently(int quantityToAdd) {
        final CountDownLatch latch = new CountDownLatch(numberOfConcurrentCustomers);

        for (int i = 0; i < numberOfConcurrentCustomers; i++) {
            final int customerIndex = i;
            executorService.submit(() -> {
                try {
                    // Simulate adding a product to the basket
                    basketItemService.addOrUpdate(customers.get(customerIndex).getCustomerId(), productIdToAdd, quantityToAdd);
                } finally {
                    latch.countDown();
                }
            });
        }

        return latch;
    }

    private CountDownLatch checkoutConcurrently() {
        final CountDownLatch latch = new CountDownLatch(numberOfConcurrentCustomers);
        for (int i = 0; i < numberOfConcurrentCustomers; i++) {
            final int customerIndex = i;
            executorService.submit(() -> {
                try {
                    checkoutService.checkout(customers.get(customerIndex).getCustomerId());
                } finally {
                    latch.countDown();
                }
            });
        }

        return latch;
    }
}