package toyproject.order.api.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import toyproject.order.api.order.dto.CreateOrderRequest;
import toyproject.order.api.order.dto.CreateOrderResponse;
import toyproject.order.service.OrderService;

@RestController
@RequiredArgsConstructor
public class OrderCommandApiController {

    private final OrderService orderService;

    @PostMapping("/api/orders")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        Long orderId = orderService.order(request.memberId(), request.itemId(), request.count());
        return ResponseEntity.ok(new CreateOrderResponse(orderId));
    }

    @PostMapping("/api/orders/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
