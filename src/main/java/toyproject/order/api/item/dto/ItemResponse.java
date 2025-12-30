package toyproject.order.api.item.dto;

public record ItemResponse(Long id, String name, int price, int stockQuantity) {
}
