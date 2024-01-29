package com.electronicsstore.dto;

import jakarta.validation.constraints.Positive;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountDto {

    @Positive
    private int threshold;

    @Positive
    private Double amount;
}