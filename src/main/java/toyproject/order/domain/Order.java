package toyproject.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 연관 관계 편의 메서드
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // 주문 생성
    public static Order createOrder(Member member, OrderItem... orderItems) {
        if (member == null) throw new IllegalArgumentException("주문자는 필수입니다.");
        if (orderItems == null || orderItems.length==0) throw new IllegalArgumentException("주문 상품은 1개 이상이어야 합니다.");

        Order order = new Order();
        order.setMember(member);
        for (OrderItem orderItem : orderItems) {
            if (orderItem == null) throw new IllegalArgumentException("주문 상품이 비어있습니다.");
            order.addOrderItem(orderItem);
        }
        order.status = OrderStatus.ORDER;
        order.orderDate = LocalDateTime.now();

        return order;
    }

    // 주문 취소
    public void cancel() {
        if (this.status == OrderStatus.CANCEL) {
            throw new IllegalStateException("이미 취소된 주문 입니다.");
        }
        this.status = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }
}
