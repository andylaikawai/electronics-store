package com.electronicsstore.repository;

import com.electronicsstore.model.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Long> {

}