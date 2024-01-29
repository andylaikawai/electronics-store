package com.electronicsstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "receipts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "receiptId")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long receiptId;

    @Column(name = "issue_date")
    @NotNull
    private LocalDateTime issueDate;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ReceiptItem> receiptItems;

    @Column(name = "total_amount")
    @NotNull
    @PositiveOrZero
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;
}
