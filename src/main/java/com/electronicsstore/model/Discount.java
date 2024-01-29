package com.electronicsstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "discounts")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "discountId")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long discountId;

    /**
     * the threshold order size for this discount to be applied.
     * e.g.
     * - if threshold = 1, discount always applies
     * - if threshold = 2, discount applies to both 2 items purchased. Remaining items, if any, will have no discount
     **/
    @Column
    @Positive
    private int threshold;

    @Column
    @NonNull
    @Positive
    private Double amount;

}


