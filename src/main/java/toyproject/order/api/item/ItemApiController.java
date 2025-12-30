package toyproject.order.api.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toyproject.order.api.item.dto.CreateItemRequest;
import toyproject.order.api.item.dto.CreateItemResponse;
import toyproject.order.api.item.dto.UpdateItemRequest;
import toyproject.order.domain.Item;
import toyproject.order.repository.ItemRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemApiController {

    private final ItemRepository itemRepository;

    @PostMapping
    public ResponseEntity<CreateItemResponse> create(@RequestBody @Valid CreateItemRequest request) {
        Item item = new Item(request.name(), request.price(), request.stockQuantity());
        itemRepository.save(item);
        return ResponseEntity.ok(new CreateItemResponse(item.getId()));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<Void> update(@PathVariable Long itemId, @RequestBody @Valid UpdateItemRequest request) {
        Item item = itemRepository.findOne(itemId);
        item.change(request.name(), request.price(), request.stockQuantity());
        return ResponseEntity.ok().build();
    }
}
