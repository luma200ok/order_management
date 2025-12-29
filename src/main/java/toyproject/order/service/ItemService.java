package toyproject.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toyproject.order.domain.Item;
import toyproject.order.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Long saveItem(Item item) {
        itemRepository.save(item);
        return item.getId();
    }

    public Item findOne(Long id) {
        return itemRepository.findOne(id);
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }
}
