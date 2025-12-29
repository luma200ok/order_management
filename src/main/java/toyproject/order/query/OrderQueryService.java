package toyproject.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toyproject.order.domain.Order;
import toyproject.order.domain.OrderItem;
import toyproject.order.query.dto.OrderDto;
import toyproject.order.query.dto.OrderItemDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderQueryRepository orderQueryRepository;

    public List<OrderDto> findOrders(int age, int size) {
        int offset = age * size;

        // 1) 주문 (To-One만) 페이징 조회
        List<Order> orders = orderQueryRepository.findOrders(offset, size);

        // 2) 주문 ID 리스트 추출
        List<Long> orderIds = orders.stream().map(Order::getId).toList();

        // 3) 주문 상품 + 상품을 IN 쿼리로 한 번에 조회
        List<OrderItem> orderItems = orderQueryRepository.findOrderItemByOrderIds(orderIds);

        // 주문이 0건이면 다음 쿼리 생략
        if (orderIds.isEmpty()) {
            return List.of();
        }

        // 4) orderId -> OrderItemDto 리스트로 그룹핑
        Map<Long, List<OrderItemDto>> itemsMap = orderItems.stream().collect(Collectors.groupingBy(
                oi -> oi.getOrder().getId(),
                Collectors.mapping(
                        oi -> new OrderItemDto(
                                oi.getItem().getName(),
                                oi.getOrderPrice(),
                                oi.getCount()
                        ),
                        toList())
        ));

        // 5) 주문 DTO 로 변환
        return orders.stream()
                .map(o -> new OrderDto(
                        o.getId(),
                        o.getMember().getName(),
                        o.getOrderDate(),
                        o.getStatus(),   // enum이면 name() 대신 그대로 내려도 됨
                        itemsMap.getOrDefault(o.getId(),List.of())
                ))
                .toList();
    }

    public List<OrderDto> findOrderSimple() {
        List<Order> orders = orderQueryRepository.findOrdersWithItems();

        return orders.stream().map(o -> new OrderDto(
                o.getId(),
                o.getMember().getName(),
                o.getOrderDate(),
                o.getStatus(),
                o.getOrderItems().stream().map(oi -> new OrderItemDto(
                        oi.getItem().getName(),
                        oi.getOrderPrice(),
                        oi.getCount()
                )).toList()
        )).toList();
    }
}
