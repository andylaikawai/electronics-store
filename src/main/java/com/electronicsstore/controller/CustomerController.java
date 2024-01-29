package com.electronicsstore.controller;

import com.electronicsstore.dto.*;
import com.electronicsstore.model.BasketItem;
import com.electronicsstore.model.Customer;
import com.electronicsstore.model.Receipt;
import com.electronicsstore.service.BasketItemService;
import com.electronicsstore.service.CheckoutService;
import com.electronicsstore.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/")
public class CustomerController {

    private final BasketItemService basketItemService;

    private final CustomerService customerService;
    private final CheckoutService checkoutService;

    @Autowired
    public CustomerController(BasketItemService basketItemService, CustomerService customerService, CheckoutService checkoutService) {
        this.basketItemService = basketItemService;
        this.customerService = customerService;
        this.checkoutService = checkoutService;
    }

    @PostMapping("/")
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDto customerDto) {
        Customer savedCustomer = customerService.createCustomer(customerDto.getName());
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }


    @PostMapping("{customerId}/basket")
    public ResponseEntity<BasketItem> addProductToBasket(@PathVariable Long customerId, @RequestBody BasketItemDto basketItemDto) {
        BasketItem basketItem = basketItemService.addOrUpdate(customerId, basketItemDto.getProductId(), basketItemDto.getQuantity());
        return new ResponseEntity<>(basketItem, HttpStatus.CREATED);
    }

    @DeleteMapping("{customerId}/basket/{productId}")
    public ResponseEntity<Void> removeProductFromBasket(@PathVariable Long customerId, @PathVariable Long productId) {
        basketItemService.remove(customerId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{customerId}/receipt")
    public ResponseEntity<Receipt> previewReceipt(@PathVariable Long customerId) {
        Receipt receipt = checkoutService.previewReceipt(customerId);
        return new ResponseEntity<>(receipt, HttpStatus.OK);
    }

    @PostMapping("{customerId}/checkout")
    public ResponseEntity<Receipt> checkout(@PathVariable Long customerId) {
        Receipt receipt = checkoutService.checkout(customerId);
        return new ResponseEntity<>(receipt, HttpStatus.OK);
    }
}