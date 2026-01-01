package toyproject.order.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toyproject.order.domain.Order;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // LAZY 로딩으로 인한 N+1 발생
    public List<Order> findAll() {
        return em.createQuery("select o from Order o", Order.class).getResultList();
    }

    // fetch join 으로 N+1 해결
    public List<Order> findAllWithItems() {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class
        ).getResultList();
    }
}
