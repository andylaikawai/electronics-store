package com.electronicsstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "basket_items")
@IdClass(BasketItemKey.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketItem {

    @Id
    @Column(name = "customer_id")
    private Long customerId;

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "quantity")
    @PositiveOrZero
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    public BigDecimal calculateDiscountedPrice() {
        Discount discount = product.getDiscount();
        BigDecimal price = product.getPrice();

        if (discount != null) {
            int discountThreshold = discount.getThreshold();
            if (quantity >= discountThreshold) {
                int ineligibleQuantity = quantity % discountThreshold;
                int eligibleQuantity = quantity - ineligibleQuantity;
                return price.multiply(BigDecimal.valueOf(ineligibleQuantity))
                        .add(price.multiply(BigDecimal.valueOf(eligibleQuantity)).multiply(BigDecimal.valueOf(discount.getAmount())));
            }
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
