package com.electronicsstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    @NotBlank
    private String name;

    @PositiveOrZero
    private BigDecimal price;

    @PositiveOrZero
    private int inventory;
}