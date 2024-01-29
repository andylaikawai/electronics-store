package com.electronicsstore.repository;

import com.electronicsstore.model.BasketItem;
import com.electronicsstore.model.BasketItemKey;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BasketItemRepository extends JpaRepository<BasketItem, BasketItemKey> {
    List<BasketItem> findByCustomerId(@NotNull Long customerId);

    Optional<BasketItem> findByCustomerIdAndProductId(@NotNull Long customerId, @NotNull Long productId);

    void deleteByCustomerIdAndProductId(@NotNull Long customerId, @NotNull Long productId);

    void deleteByCustomerId(@NotNull Long customerId);

    void deleteByProductId(@NotNull Long productId);
}