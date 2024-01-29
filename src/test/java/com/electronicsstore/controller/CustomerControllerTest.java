package com.electronicsstore.controller;

import com.electronicsstore.dto.*;
import com.electronicsstore.model.*;
import com.electronicsstore.service.BasketItemService;
import com.electronicsstore.service.CheckoutService;
import com.electronicsstore.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BasketItemService basketItemService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CheckoutService checkoutService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void whenCreateCustomer_thenReturns201() throws Exception {
        CustomerDto customerDto = CustomerDto.builder().name("Test Customer").build();
        Customer savedCustomer = Customer.builder().name("Test Customer").build();

        when(customerService.createCustomer(anyString()))
                .thenReturn(savedCustomer);

        mockMvc.perform(post("/api/customers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(savedCustomer)));
    }

    @Test
    void whenAddProductToBasket_thenReturns201() throws Exception {
        Long customerId = 1L;
        BasketItemDto basketItemDto = BasketItemDto.builder()
                .productId(1L)
                .quantity(2)
                .build();
        BasketItem basketItem = BasketItem.builder()
                .productId(1L)
                .quantity(2)
                .customerId(1L)
                .build();

        when(basketItemService.addOrUpdate(eq(customerId), anyLong(), anyInt()))
                .thenReturn(basketItem);

        mockMvc.perform(post("/api/customers/{customerId}/basket", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(basketItemDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(basketItem)));
    }

    @Test
    void whenRemoveProductFromBasket_thenReturns200() throws Exception {
        Long customerId = 1L;
        Long productId = 1L;

        doNothing().when(basketItemService).remove(eq(customerId), anyLong());

        mockMvc.perform(delete("/api/customers/{customerId}/basket/{productId}", customerId, productId))
                .andExpect(status().isOk());
    }

    @Test
    void whenPreviewReceipt_thenReturns200() throws Exception {
        Long customerId = 1L;
        Receipt receipt = buildReceipt();

        when(checkoutService.previewReceipt(customerId)).thenReturn(receipt);

        mockMvc.perform(get("/api/customers/{customerId}/receipt", customerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(receipt)));
    }

    @Test
    void whenCheckout_thenReturns200() throws Exception {
        Long customerId = 1L;
        Receipt receipt = buildReceipt();

        when(checkoutService.checkout(customerId)).thenReturn(receipt);

        mockMvc.perform(post("/api/customers/{customerId}/checkout", customerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(receipt)));
    }

    private Receipt buildReceipt() {
        return Receipt.builder()
                .receiptItems(List.of(
                                ReceiptItem.builder()
                                        .productName("Test Product")
                                        .unitPrice(BigDecimal.TEN)
                                        .quantity(1)
                                        .discountedPrice(BigDecimal.TEN)
                                        .build()
                        )
                )
                .totalPrice(BigDecimal.ONE)
                .build();
    }

}