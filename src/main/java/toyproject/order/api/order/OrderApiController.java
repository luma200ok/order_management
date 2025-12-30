package toyproject.order.api.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import toyproject.order.query.OrderQueryService;
import toyproject.order.query.dto.OrderDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderQueryService orderQueryService;

    @GetMapping("/api/orders")
    public List<OrderDto> orders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size

    ) {
        return orderQueryService.findOrders(page, size);
    }

    @GetMapping("/api/orders/simple")
    public List<OrderDto> ordersSimple() {
        return orderQueryService.findOrderSimple();
    }
}
