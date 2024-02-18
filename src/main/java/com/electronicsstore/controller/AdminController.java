package com.electronicsstore.controller;

import com.electronicsstore.dto.DiscountDto;
import com.electronicsstore.dto.ProductDto;
import com.electronicsstore.model.Customer;
import com.electronicsstore.model.Discount;
import com.electronicsstore.model.Product;
import com.electronicsstore.model.Receipt;
import com.electronicsstore.service.CheckoutService;
import com.electronicsstore.service.CustomerService;
import com.electronicsstore.service.DiscountService;
import com.electronicsstore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/")
public class AdminController {

    private final ProductService productService;
    private final DiscountService discountService;

    private final CheckoutService checkoutService;

    private final CustomerService customerService;

    @Autowired
    public AdminController(ProductService productService, DiscountService discountService, CustomerService customerService, CheckoutService checkoutService) {
        this.productService = productService;
        this.customerService = customerService;
        this.discountService = discountService;
        this.checkoutService = checkoutService;
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("/receipts")
    public ResponseEntity<List<Receipt>> getReceipts() {
        List<Receipt> receipts = checkoutService.getAllReceipts();
        return new ResponseEntity<>(receipts, HttpStatus.OK);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
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

    @GetMapping("/discounts")
    public ResponseEntity<List<Discount>> getDiscounts() {
        List<Discount> discounts = discountService.getAllDiscounts();
        return new ResponseEntity<>(discounts, HttpStatus.OK);
    }

    @PostMapping("/discounts/add-to-product/{productId}")
    public ResponseEntity<Discount> addDiscountToProduct(@PathVariable Long productId, @RequestBody DiscountDto discountDto) {
        Discount createdDiscount = discountService.addDiscountToProduct(productId, discountDto.getThreshold(), discountDto.getAmount());
        return new ResponseEntity<>(createdDiscount, HttpStatus.CREATED);
    }

    @DeleteMapping("/discounts/{discountId}")
    public ResponseEntity<Void> removeDiscount(@PathVariable Long discountId) {
        discountService.remove(discountId);
        return ResponseEntity.ok().build();
    }
}
