package com.electronicsstore.service;

import com.electronicsstore.model.BasketItem;
import com.electronicsstore.model.Receipt;
import com.electronicsstore.model.ReceiptItem;
import com.electronicsstore.repository.BasketItemRepository;
import com.electronicsstore.repository.ProductRepository;
import com.electronicsstore.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckoutService {

    private final BasketItemRepository basketItemRepository;
    private final ProductRepository productRepository;
    private final ReceiptRepository receiptRepository;

    @Autowired
    public CheckoutService(BasketItemRepository basketItemRepository,
                           ProductRepository productRepository,
                           ReceiptRepository receiptRepository) {
        this.basketItemRepository = basketItemRepository;
        this.productRepository = productRepository;
        this.receiptRepository = receiptRepository;
    }

    @Transactional
    public Receipt checkout(Long customerId) {

        List<BasketItem> basketItems = basketItemRepository.findByCustomerId(customerId);

        Receipt receipt = createReceipt(basketItems);

        receipt = receiptRepository.save(receipt);

        // TODO include logic to update the inventory

        return receipt;
    }

    private BigDecimal calculateTotalAmount(List<BasketItem> basketItems) {
        return basketItems.stream()
                .map(item -> {
                    BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                    return item.getProduct().getPrice().multiply(quantity); // TODO discount
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Receipt createReceipt(List<BasketItem> basketItems) {
        BigDecimal totalPrice = calculateTotalAmount(basketItems);
        Receipt receipt = Receipt.builder()
                .totalPrice(totalPrice)
                .issueDate(LocalDateTime.now())
                .build();

        List<ReceiptItem> receiptItems = basketItems.stream().map(basketItem -> ReceiptItem.builder()
                .product(basketItem.getProduct())
                .quantity(basketItem.getQuantity())
                .receipt(receipt)
                .build()).toList();

        receipt.setReceiptItems(receiptItems);

        return receipt;
    }
}