package com.electronicsstore.service;

import com.electronicsstore.model.BasketItem;
import com.electronicsstore.model.Customer;
import com.electronicsstore.model.Product;
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


/**
 * This test aims to ensure services are transactional and thread-safe.
 * It simulates 10 concurrent customers adding a product into their baskets.
 * Then it simulates simultaneous checkout, which updates product inventory concurrently
 */
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

    private final int numberOfConcurrentCustomers = 5;

    private final BigDecimal productUnitPrice = BigDecimal.valueOf(9.99);

    private final ExecutorService executorService = Executors.newFixedThreadPool(numberOfConcurrentCustomers);

    private final List<Long> customerIds = new ArrayList<>();

    private Long productId;

    private final int quantityOfProductToAdd = 2;


    @BeforeEach
    public void setUp() {
        // Initialize customers and product
        productId = productService.createProduct("MOCK_PRODUCT", productUnitPrice, 20).getProductId();
        for (int i = 0; i < numberOfConcurrentCustomers; i++) {
            Customer customer = customerService.createCustomer("MOCK-CUSTOMER-" + i);
            customerIds.add(customer.getCustomerId());
        }
    }

    @Test
    public void testConcurrentBasketOperations() throws InterruptedException {
        int timeoutInMinutes = 1;

        CountDownLatch addBasketItemsLatch = new CountDownLatch(numberOfConcurrentCustomers);
        addItemsToBasketConcurrently(addBasketItemsLatch);
        boolean finished = addBasketItemsLatch.await(timeoutInMinutes, TimeUnit.MINUTES);
        assertTrue(finished, "Not all operations finished in the expected timeframe");

        // check the state of each basket after adding items
        for (Long customerId : customerIds) {
            List<BasketItem> addedItem = basketItemService.getBasketItemsByCustomerId(customerId);
            assertEquals(1, addedItem.size());
            assertEquals(productId, addedItem.getFirst().getProductId());
            assertEquals(quantityOfProductToAdd, addedItem.getFirst().getQuantity());
        }


        CountDownLatch checkoutLatch = new CountDownLatch(numberOfConcurrentCustomers);
        checkoutConcurrently(checkoutLatch);
        finished = checkoutLatch.await(timeoutInMinutes, TimeUnit.MINUTES);
        assertTrue(finished, "Not all checkout operations finished in the expected timeframe");

        executorService.shutdown();

        // check that basketItems are cleared and product inventory are updated correctly
        // check the state of each basket after adding items
        for (Long customerId : customerIds) {
            List<BasketItem> remainingItems = basketItemService.getBasketItemsByCustomerId(customerId);
            assertEquals(0, remainingItems.size());
        }

        Product productAfterCheckout = productService.getProductById(productId);
        assertEquals(10, productAfterCheckout.getInventory());

    }

    private void addItemsToBasketConcurrently(CountDownLatch latch) {
        for (int i = 0; i < numberOfConcurrentCustomers; i++) {
            final int customerIndex = i;
            executorService.submit(() -> {
                try {
                    System.out.println("Adding product for customer index: " + customerIndex);
                    basketItemService.addOrUpdate(customerIds.get(customerIndex), productId, quantityOfProductToAdd);
                } catch (Exception exception) {
                    System.err.println("Exception thrown when adding product for customer index: " + customerIndex);
                    System.err.println(exception.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
    }

    private void checkoutConcurrently(CountDownLatch latch) {
        for (int i = 0; i < numberOfConcurrentCustomers; i++) {
            final int customerIndex = i;
            executorService.submit(() -> {
                try {
                    System.out.println("Checking out for customer index: " + customerIndex);
                    checkoutService.checkout(customerIds.get(customerIndex));
                } catch (Exception exception) {
                    System.err.println("Exception thrown when checking out for customer index: " + customerIndex);
                    System.err.println(exception.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
    }
}