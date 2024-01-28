package com.electronicsstore.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "baskets")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BasketItem> items = new HashSet<>();

    public void addItem(BasketItem item) {
        items.add(item);
        item.setBasket(this);
    }

    public void removeItem(BasketItem item) {
        items.remove(item);
        item.setBasket(null);
    }
}