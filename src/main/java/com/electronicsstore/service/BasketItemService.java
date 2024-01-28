package com.electronicsstore.service;

import com.electronicsstore.model.BasketItem;
import com.electronicsstore.repository.BasketItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BasketItemService {

    private final BasketItemRepository basketItemRepository;

    public BasketItemService(BasketItemRepository basketItemRepository) {
        this.basketItemRepository = basketItemRepository;
    }

    @Transactional(readOnly = true)
    public BasketItem getBasketItemById(Long id) {
        return basketItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BasketItem not found with id: " + id));
    }

    @Transactional
    public BasketItem createOrUpdateBasketItem(BasketItem basketItem) {
        return basketItemRepository.save(basketItem);
    }

    @Transactional
    public void deleteBasketItem(Long id) {
        basketItemRepository.deleteById(id);
    }
}
