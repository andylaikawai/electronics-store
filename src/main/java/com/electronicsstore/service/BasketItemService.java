package com.electronicsstore.service;

import com.electronicsstore.model.BasketItem;
import com.electronicsstore.repository.BasketItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BasketItemService {

    private final BasketItemRepository basketItemRepository;

    public BasketItemService(BasketItemRepository basketItemRepository) {
        this.basketItemRepository = basketItemRepository;
    }

    @Transactional(readOnly = true)
    public List<BasketItem> getBasketItemsByCustomerId(Long customerId) {
        return basketItemRepository.findByCustomerId(customerId);
    }

    @Transactional
    public BasketItem addOrUpdate(Long customerId, Long productId, int quantity) {
        BasketItem existingBasketItem = basketItemRepository.findByCustomerIdAndProductId(customerId, productId).orElse(null);
        if (existingBasketItem != null) {
            // add quantity
            existingBasketItem.setQuantity(existingBasketItem.getQuantity() + quantity);
            return basketItemRepository.save(existingBasketItem);
        } else {
            BasketItem newBasketItem = BasketItem.builder()
                    .customerId(customerId)
                    .productId(productId)
                    .quantity(quantity)
                    .build();
            return basketItemRepository.save(newBasketItem);
        }
    }

    @Transactional
    public void remove(Long customerId, Long productId) {
        BasketItem existingBasketItem = basketItemRepository.findByCustomerIdAndProductId(customerId, productId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("BasketItem not found with customer id: %d and product id: %d", customerId, productId)));

        basketItemRepository.deleteByCustomerIdAndProductId(existingBasketItem.getCustomerId(), existingBasketItem.getProductId());
    }
}
