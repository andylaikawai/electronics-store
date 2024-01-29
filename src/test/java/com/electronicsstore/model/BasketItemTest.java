package com.electronicsstore.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BasketItemTest {

    private final BigDecimal unitPrice = BigDecimal.TEN;
    private final int quantity = 10;

    @ParameterizedTest(name = "calculate discounted price given a discount")
    @MethodSource("discountProvider")
    void calculateDiscountedPrice(Discount discount, BigDecimal expected) {
        Product product = Product.builder()
                .name("Test Product")
                .price(unitPrice)
                .inventory(99)
                .discount(discount)
                .build();
        BasketItem basketItem = BasketItem.builder()
                .quantity(quantity)
                .product(product)
                .build();

        assertThat(basketItem.calculateDiscountedPrice()).isEqualByComparingTo(expected);
    }

    private static Stream<Arguments> discountProvider() {
        return Stream.of(
                Arguments.of(null, BigDecimal.valueOf(100)),
                Arguments.of(Discount.builder().threshold(1).amount(1.0).build(), BigDecimal.valueOf(100)),
                Arguments.of(Discount.builder().threshold(1).amount(0.9).build(), BigDecimal.valueOf(90)),
                Arguments.of(Discount.builder().threshold(2).amount(1.0).build(), BigDecimal.valueOf(100)),
                Arguments.of(Discount.builder().threshold(2).amount(0.9).build(), BigDecimal.valueOf(90)),
                Arguments.of(Discount.builder().threshold(2).amount(0.75).build(), BigDecimal.valueOf(75)), // i.e. buy 1 get 50% off the second
                Arguments.of(Discount.builder().threshold(3).amount(0.9).build(), BigDecimal.valueOf(91)), // 10*9*0.9 + 10
                Arguments.of(Discount.builder().threshold(4).amount(0.9).build(), BigDecimal.valueOf(92)), // 10*8*0.9 + 10*2
                Arguments.of(Discount.builder().threshold(10).amount(0.5).build(), BigDecimal.valueOf(50)), // 10*10*0.5
                Arguments.of(Discount.builder().threshold(11).amount(0.5).build(), BigDecimal.valueOf(100))
        );
    }
}