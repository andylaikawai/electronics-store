package com.electronicsstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

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
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}