package toyproject.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toyproject.order.domain.Order;
import toyproject.order.domain.OrderItem;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    // 1) ToOne(member)만 fetch join +페이징
    public List<Order> findOrders(int offSet, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m", Order.class)
                .setFirstResult(offSet)
                .setMaxResults(limit)
                .getResultList();
    }

    // 2) 컬렉션(OrderItems)은 IN 으로 한번에 조회
    public List<OrderItem> findOrderItemByOrderIds(List<Long> orderIds) {
        return em.createQuery(
                        "select oi from OrderItem oi" +
                                " join fetch oi.item i" +
                                " where oi.order.id in :orderIds", OrderItem.class)
                .setParameter("orderIds", orderIds)
                .getResultList();
    }

    public List<Order> findOrdersWithItems() {
        return em.createQuery(
                        "select distinct o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.orderItems oi" +
                                " join fetch oi.item i", Order.class)
                .getResultList();
    }

}
