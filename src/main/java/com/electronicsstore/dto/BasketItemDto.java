package com.electronicsstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasketItemDto {

    @NotNull
    private Long productId;

    @PositiveOrZero
    private int quantity;

}

