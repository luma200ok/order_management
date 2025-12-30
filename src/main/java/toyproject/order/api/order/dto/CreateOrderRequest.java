package toyproject.order.api.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull Long memberId,
        @NotNull Long itemId,
        @Min(1) int count
) {

}
