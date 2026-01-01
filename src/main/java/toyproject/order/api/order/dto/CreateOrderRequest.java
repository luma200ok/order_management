package toyproject.order.api.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull Long memberId,
        @NotNull Long itemId,
        @Min(value = 1, message = "주문 수량은 1 이상이어야 합니다.")
        int count
) {

}
