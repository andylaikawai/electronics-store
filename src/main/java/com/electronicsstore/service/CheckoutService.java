package com.electronicsstore.service;

import com.electronicsstore.model.*;
import com.electronicsstore.repository.BasketItemRepository;
import com.electronicsstore.repository.CustomerRepository;
import com.electronicsstore.repository.ProductRepository;
import com.electronicsstore.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutService {
    private final BasketItemRepository basketItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ReceiptRepository receiptRepository;

    @Autowired
    public CheckoutService(BasketItemRepository basketItemRepository,
                           CustomerRepository customerRepository,
                           ProductRepository productRepository,
                           ReceiptRepository receiptRepository) {
        this.basketItemRepository = basketItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.receiptRepository = receiptRepository;
    }

    @Transactional(readOnly = true)
    public List<Receipt> getAllReceipts() {
        return receiptRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Receipt previewReceipt(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() ->
                new RuntimeException("Customer not found with ID: " + customerId));

        List<BasketItem> basketItems = basketItemRepository.findByCustomerId(customerId);
        return createReceipt(customer, basketItems);
    }

    /***
     * We use optimistic lock to prevent concurrent checkout that lead to dirty read
     */
    @Transactional
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public Receipt checkout(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() ->
                new RuntimeException("Customer not found with ID: " + customerId));

        List<BasketItem> basketItems = basketItemRepository.findByCustomerId(customerId);

        Receipt receipt = createReceipt(customer, basketItems);

        updateProductInventory(basketItems);
        clearBasketItems(customerId);
        receiptRepository.save(receipt);

        return receipt;
    }

    private BigDecimal calculateTotalPrice(List<BasketItem> basketItems) {
        return basketItems.stream()
                .map(BasketItem::calculateDiscountedPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Receipt createReceipt(Customer customer, List<BasketItem> basketItems) {
        BigDecimal totalPrice = calculateTotalPrice(basketItems);
        Receipt receipt = Receipt.builder()
                .totalPrice(totalPrice)
                .issueDate(LocalDateTime.now())
                .customer(customer)
                .receiptItems(new ArrayList<>())
                .build();

        List<ReceiptItem> receiptItems = basketItems.stream().map(basketItem -> {
            ReceiptItem item = ReceiptItem.builder()
                    .productName(basketItem.getProduct().getName())
                    .unitPrice(basketItem.getProduct().getPrice())
                    .quantity(basketItem.getQuantity())
                    .discountedPrice(basketItem.calculateDiscountedPrice())
                    .build();

            Discount discount = basketItem.getProduct().getDiscount();
            if (discount != null) {
                item.setDiscountThreshold(discount.getThreshold());
                item.setDiscountAmount(discount.getAmount());
            }
            return item;
        }).toList();

        receipt.setReceiptItems(receiptItems);

        return receipt;
    }

    private void updateProductInventory(List<BasketItem> basketItems) {
        for (BasketItem item : basketItems) {
            Product product = item.getProduct();
            int quantityToDeduct = item.getQuantity();

            if (product.getInventory() < quantityToDeduct) {
                throw new RuntimeException("Insufficient inventory for product: " + product.getProductId());
            }

            product.setInventory(product.getInventory() - quantityToDeduct);
            productRepository.save(product);
        }
    }

    private void clearBasketItems(Long customerId) {
        basketItemRepository.deleteByCustomerId(customerId);
    }
}