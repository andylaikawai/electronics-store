package com.electronicsstore.service;

import com.electronicsstore.model.Basket;
import com.electronicsstore.model.Receipt;
import com.electronicsstore.repository.BasketRepository;
import com.electronicsstore.repository.ProductRepository;
import com.electronicsstore.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CheckoutService {

    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;
    private final ReceiptRepository receiptRepository;

    @Autowired
    public CheckoutService(BasketRepository basketRepository,
                           ProductRepository productRepository,
                           ReceiptRepository receiptRepository) {
        this.basketRepository = basketRepository;
        this.productRepository = productRepository;
        this.receiptRepository = receiptRepository;
    }

    @Transactional
    public Receipt checkoutBasket(Long basketId) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new RuntimeException("Basket not found"));


        Receipt receipt = createReceipt(basket);

        receipt = receiptRepository.save(receipt);

        // TODO include logic to update the inventory, if necessary

        return receipt;
    }

    private BigDecimal calculateTotalAmount(Basket basket) {
        return basket.getItems().stream()
                .map(item -> {
                    BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                    return item.getProduct().getPrice().multiply(quantity);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Receipt createReceipt(Basket basket) {
        BigDecimal totalAmount = calculateTotalAmount(basket);

        Receipt receipt = new Receipt();
        receipt.setBasket(basket); // TODO recreate new basket for the customer?
        receipt.setTotalAmount(totalAmount);
        receipt.setIssueDate(LocalDateTime.now());
        return receipt;
    }
}