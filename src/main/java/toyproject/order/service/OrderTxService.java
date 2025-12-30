package toyproject.order.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toyproject.order.domain.Item;
import toyproject.order.domain.Member;
import toyproject.order.domain.Order;
import toyproject.order.domain.OrderItem;
import toyproject.order.repository.ItemRepository;
import toyproject.order.repository.MemberRepository;
import toyproject.order.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderTxService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final EntityManager em;

    @Transactional
    public Long orderWithOptimistic(Long memberId, Long itemId, int count) {
        Member member = memberRepository.findOne(memberId);

        Item item = itemRepository.findOneWithOptimisticLock(itemId);
        item.removeStock(count);

        OrderItem orderItem = new OrderItem(item, item.getPrice(), count);
        Order order = Order.createOrder(member, orderItem);
        orderRepository.save(order);

        em.flush();

        return order.getId();

    }
}
