package com.electronicsstore.dto;

import com.electronicsstore.model.Customer;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptItemDto {
    private int quantity;
    private ProductDto product;

}