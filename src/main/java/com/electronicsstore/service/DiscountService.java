package com.electronicsstore.service;

import com.electronicsstore.model.Product;
import com.electronicsstore.model.Discount;
import com.electronicsstore.repository.DiscountRepository;
import com.electronicsstore.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository, ProductRepository productRepository) {
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Product addDiscountToProduct(Long productId, Discount discount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID: " + productId + " not found."));

        Discount savedDiscount = discountRepository.save(discount);
        product.setDiscount(savedDiscount);

        return productRepository.save(product);
    }

    @Transactional
    public void remove(Long discountId) {
        discountRepository.findById(discountId).orElseThrow(() -> new EntityNotFoundException("Discount with ID: " + discountId + " not found."));
        discountRepository.deleteById(discountId);
    }

}
