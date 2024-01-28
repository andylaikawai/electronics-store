package com.electronicsstore.repository;

import com.electronicsstore.model.Receipt;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByCustomer_CustomerId(@NotNull Long customerId);
}