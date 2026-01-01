package toyproject.order.api.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Orders", description = "주문 조회 생성/취소 + 주문 조회 (V1~V4 비교)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryService orderQueryService;

    @Operation(summary = "주문 간단 조회", description = "주문 + 회원 기본 정보 조회.")
    @GetMapping("/orders/simple")
    public List<OrderQueryDto> ordersSimple() {
        return orderQueryService.findOrderSimple();
    }

    @Operation(
            summary = "[Query V4] 주문 조회 (페이징/최적화)"
            , description = "페이징 기반으로 주문 + 주문상품 정보 조회.(최적화 버전")
    @GetMapping("/orders/v4")
    public List<OrderQueryDto> v4(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return orderQueryService.findOrders(page, size);
    }

    @Operation(
            summary = "[DEPRECATED] V1 - 문제 재현용",
            description = "엔티티 직접 반환 / N+1 및 무한참조 문제 재현. 실제 사용 비권장")
    @Deprecated
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

    @Operation(
            summary = "[Query V2] 주문 조회 (DTO 변환)"
            , description = "엔티티 조회 후 DTO로 변환. (N+1가능)")
    @GetMapping("/orders/v2")
    public List<OrderResponse> v2() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::new)
                .toList();
    }

    @Operation(summary = "[Query V3] 주문 조회 (fetch join)"
            , description = "fetch join으로 연관 데이터 한번에 조회.(컬렉션 fetch join -> 중복 row 가능")
    @GetMapping("/orders/v3")
    public List<OrderResponse> v3() {
        return orderRepository.findAllWithItems().stream()
                .map(OrderResponse::new)
                .toList();
    }

    @GetMapping("/test")
    public void test() {
        throw new IllegalArgumentException("test");
    }
}
