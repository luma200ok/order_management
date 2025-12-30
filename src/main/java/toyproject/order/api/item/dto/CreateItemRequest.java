package toyproject.order.api.item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateItemRequest(
        @NotBlank String name,
        @Min(0) int price,
        @Min(0) int stockQuantity
) {
}
