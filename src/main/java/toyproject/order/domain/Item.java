package toyproject.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    public Item(String name, int price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
/*

    @Version
    private Long version; // 낙관적 락 핵심
*/

    // 재고 차감
    public void removeStock(int quantity) {
        int rest = this.stockQuantity - quantity;
        if (rest < 0) throw new IllegalStateException("재고 부족");
        this.stockQuantity = rest;
    }

    // 재고 복구
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void change(String name, int price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

}
