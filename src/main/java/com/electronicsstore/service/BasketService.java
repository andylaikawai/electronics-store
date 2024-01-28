package com.electronicsstore.service;

import com.electronicsstore.model.Basket;
import com.electronicsstore.repository.BasketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BasketService {

    private final BasketRepository basketRepository;

    @Autowired
    public BasketService(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    @Transactional(readOnly = true)
    public Basket getBasketById(Long id) {
        return basketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Basket not found with id: " + id));
    }

    @Transactional
    public Basket createOrUpdateBasket(Basket basket) {
        return basketRepository.save(basket);
    }

    @Transactional
    public void deleteBasket(Long id) {
        basketRepository.deleteById(id);
    }

}