package com.electronicsstore.service;

import com.electronicsstore.model.Product;
import com.electronicsstore.model.Discount;
import com.electronicsstore.repository.DiscountRepository;
import com.electronicsstore.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiscountService {
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository, ProductRepository productRepository) {
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }


    @Transactional
    public Discount addDiscountToProduct(Long productId, int threshold, Double amount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID: " + productId + " not found."));

        Discount savedDiscount = discountRepository.save(Discount.builder()
                .threshold(threshold)
                .amount(amount)
                .build());
        product.setDiscount(savedDiscount);
        productRepository.save(product);

        return savedDiscount;
    }

    @Transactional
    public void remove(Long discountId) {
        discountRepository.findById(discountId).orElseThrow(() -> new EntityNotFoundException("Discount with ID: " + discountId + " not found."));
        discountRepository.deleteById(discountId);
    }

}
