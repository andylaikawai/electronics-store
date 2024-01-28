package com.electronicsstore.repository;

import com.electronicsstore.model.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {

}