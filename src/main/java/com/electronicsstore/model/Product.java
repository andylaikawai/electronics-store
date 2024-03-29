package com.electronicsstore.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "productId")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @Column(name = "name")
    @NonNull
    private String name;

    @Column(name = "price")
    @NonNull
    @Positive
    private BigDecimal price;

    @Column(name = "inventory")
    @PositiveOrZero
    @NonNull
    private int inventory;

    @OneToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;

    @Version
    private int version;
}
