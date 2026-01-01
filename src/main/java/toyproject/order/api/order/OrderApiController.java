package toyproject.order.api.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import toyproject.order.api.order.dto.OrderResponse;
import toyproject.order.domain.Order;
import toyproject.order.query.OrderQueryService;
import toyproject.order.query.dto.OrderQueryDto;
import toyproject.order.repository.OrderRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryService orderQueryService;

    @GetMapping("/orders")
    public List<OrderQueryDto> orders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return orderQueryService.findOrders(page, size);
    }

    @GetMapping("/orders/simple")
    public List<OrderQueryDto> ordersSimple() {
        return orderQueryService.findOrderSimple();
    }

    @GetMapping("/orders/v1")
    public List<Order> v1() {
        List<Order> orders = orderRepository.findAll(); // 또는 기존 조회 메서드
        // Lazy 강제 초기화 (JSON 직렬화/프록시 문제 방지)
        for (Order o : orders) {
            o.getMember().getName();
            o.getOrderItems().forEach(oi -> oi.getItem().getName());
        }
        return orders;
    }

    @GetMapping("/orders/v2")
    public List<OrderResponse> v2() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::new)
                .toList();
    }

    @GetMapping("/orders/v3")
    public List<OrderResponse> v3() {
        return orderRepository.findAllWithItems().stream()
                .map(OrderResponse::new)
                .toList();
    }
}
