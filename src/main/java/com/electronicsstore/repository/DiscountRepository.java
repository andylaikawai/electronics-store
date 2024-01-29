package com.electronicsstore.repository;

import com.electronicsstore.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

}