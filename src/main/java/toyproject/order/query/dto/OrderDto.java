package toyproject.order.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import toyproject.order.domain.OrderItem;
import toyproject.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderDto {
    private Long orderId;
    private String memberName;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderItemDto> items;
}
