package com.electronicsstore.service;

import com.electronicsstore.model.Discount;
import com.electronicsstore.model.Product;
import com.electronicsstore.repository.BasketItemRepository;
import com.electronicsstore.repository.DiscountRepository;
import com.electronicsstore.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final BasketItemRepository basketItemRepository;

    private final DiscountRepository discountRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, BasketItemRepository basketItemRepository, DiscountRepository discountRepository) {
        this.productRepository = productRepository;
        this.basketItemRepository = basketItemRepository;
        this.discountRepository = discountRepository;
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public Product createProduct(String name, BigDecimal price, int inventory) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .inventory(inventory)
                .build();
        return productRepository.save(product);
    }

    @Transactional
    public void removeProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        basketItemRepository.deleteByProductId(id);

        Discount discount = product.getDiscount();
        if (discount != null) {
            discountRepository.delete(discount);
        }

        productRepository.delete(product);
    }


}