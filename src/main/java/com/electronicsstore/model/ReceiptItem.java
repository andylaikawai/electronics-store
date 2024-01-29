package com.electronicsstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "receipt_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "receiptItemId")
public class ReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long receiptItemId;

    @Column(name = "quantity")
    @PositiveOrZero
    private int quantity;

    @Column
    @NonNull
    private String productName;

    @Column
    @NonNull
    @Positive
    private BigDecimal unitPrice;

    @Column
    @PositiveOrZero
    private int discountThreshold;

    @Column
    @Positive
    private Double discountAmount;

    @Column
    @NonNull
    @Positive
    private BigDecimal discountedPrice;
}