package toyproject.order.api.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Items", description = "상품 등록/조회/수정")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemApiController {

    private final ItemService itemService;

    @Operation(summary = "아이템 생성", description = "상품 정보를 입력받아 상품 등록.")
    @PostMapping
    public ResponseEntity<CreateItemResponse> create(@RequestBody @Valid CreateItemRequest request) {
        Long id = itemService.create(request.name(), request.price(), request.stockQuantity());
        return ResponseEntity.ok(new CreateItemResponse(id));
    }

    @Operation(summary = "주문 수정" , description = "상품 ID로 상품 정보 수정")
    @PutMapping("/{itemId}")
    public ResponseEntity<Void> update(@PathVariable Long itemId, @RequestBody @Valid UpdateItemRequest request) {
        Item item = itemService.findOne(itemId);
        item.change(request.name(), request.price(), request.stockQuantity());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "주문 조회", description = "등록된 상품 목록 조회.")
    @GetMapping
    public List<ItemResponse> list() {
        return itemService.findAll().stream()
                .map(i -> new ItemResponse(i.getId(), i.getName(), i.getPrice(), i.getStockQuantity()))
                .toList();
    }
}
