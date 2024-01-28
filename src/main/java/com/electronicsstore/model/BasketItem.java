package com.electronicsstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

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
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
}
