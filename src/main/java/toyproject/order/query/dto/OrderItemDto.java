package toyproject.order.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemDto {

    private String itemName;
    private int price;
    private int count;
}
