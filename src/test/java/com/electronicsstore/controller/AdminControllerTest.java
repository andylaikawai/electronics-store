package com.electronicsstore.controller;

import com.electronicsstore.model.Product;
import com.electronicsstore.service.ProductService;
import com.electronicsstore.service.DiscountService;
import com.electronicsstore.dto.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private DiscountService discountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void whenCreateProduct_thenReturns201() throws Exception {
        ProductDto productDto = ProductDto.builder().name("Test Product").price(BigDecimal.TEN).inventory(99).build();
        Product createdProduct = Product.builder().name("Test Product").price(BigDecimal.TEN).inventory(99).build();

        when(productService.createProduct(anyString(), any(), anyInt()))
                .thenReturn(createdProduct);

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(createdProduct)));
    }

    @Test
    void whenRemoveProduct_thenReturns200() throws Exception {
        Long productId = 1L;

        doNothing().when(productService).removeProduct(productId);

        mockMvc.perform(delete("/api/admin/products/{productId}", productId))
                .andExpect(status().isOk());
    }
}