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
public class ReceiptDto {
    private LocalDateTime issueDate;
    private List<ReceiptItemDto> receiptItems;
    private BigDecimal totalPrice;
    private Customer customer;

}