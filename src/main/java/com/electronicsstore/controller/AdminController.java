package com.electronicsstore.controller;

import com.electronicsstore.dto.ProductDto;
import com.electronicsstore.model.Discount;
import com.electronicsstore.model.Product;
import com.electronicsstore.service.DiscountService;
import com.electronicsstore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/")
public class AdminController {

    private final ProductService productService;
    private final DiscountService discountService;

    @Autowired
    public AdminController(ProductService productService, DiscountService discountService) {
        this.productService = productService;
        this.discountService = discountService;
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody ProductDto productDto) {
        Product createdProduct = productService.createProduct(productDto.getName(), productDto.getPrice(), productDto.getInventory());
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> removeProduct(@PathVariable Long productId) {
        productService.removeProduct(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/discounts/add-to-product/{productId}")
    public ResponseEntity<Discount> addDiscountToProduct(@PathVariable Long productId, @RequestBody Discount discount) {
        Discount createdDiscount = discountService.addDiscountToProduct(productId, discount.getThreshold(), discount.getAmount());
        return new ResponseEntity<>(createdDiscount, HttpStatus.CREATED);
    }

    // API to remove a discount
    @DeleteMapping("/discounts/{discountId}")
    public ResponseEntity<Void> removeDiscount(@PathVariable Long discountId) {
        discountService.remove(discountId);
        return ResponseEntity.ok().build();
    }
}
