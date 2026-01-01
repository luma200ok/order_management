package toyproject.order.api.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toyproject.order.api.order.dto.CancelOrderResponse;
import toyproject.order.api.order.dto.CreateOrderRequest;
import toyproject.order.api.order.dto.CreateOrderResponse;
import toyproject.order.service.OrderService;

@Tag(name = "Orders", description = "주문 조회 API (조회/생성/취소)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderCommandApiController {

    private final OrderService orderService;

    @Operation(summary = "[Command] 주문 생성", description = "회원/상품/수량 을 입력받아 주문 생성.")
    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        Long orderId = orderService.order(request.memberId(), request.itemId(), request.count());
        return ResponseEntity.ok(new CreateOrderResponse(orderId));
    }
    @Operation(summary = "[Command] 주문 취소", description = "주문ID로 주문 취소.")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<CancelOrderResponse> cancel(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(new CancelOrderResponse(orderId, "CANCEL"));
    }

//    @PostMapping("/api/orders/{orderId}/cancel")
//    public ResponseEntity<Void> cancel(@PathVariable Long orderId) {
//        orderService.cancelOrder(orderId);
//        return ResponseEntity.ok().build();
//    }

}
