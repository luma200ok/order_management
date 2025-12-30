package toyproject.order.api.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toyproject.order.api.item.dto.CreateItemRequest;
import toyproject.order.api.item.dto.CreateItemResponse;
import toyproject.order.api.item.dto.ItemResponse;
import toyproject.order.api.item.dto.UpdateItemRequest;
import toyproject.order.domain.Item;
import toyproject.order.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemApiController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<CreateItemResponse> create(@RequestBody @Valid CreateItemRequest request) {
        Long id = itemService.create(request.name(), request.price(), request.stockQuantity());
        return ResponseEntity.ok(new CreateItemResponse(id));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<Void> update(@PathVariable Long itemId, @RequestBody @Valid UpdateItemRequest request) {
        Item item = itemService.findOne(itemId);
        item.change(request.name(), request.price(), request.stockQuantity());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<ItemResponse> list() {
        return itemService.findAll().stream()
                .map(i -> new ItemResponse(i.getId(), i.getName(), i.getPrice(), i.getStockQuantity()))
                .toList();
    }
}
