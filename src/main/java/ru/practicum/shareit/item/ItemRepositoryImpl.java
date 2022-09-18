package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final List<Item> items = new ArrayList<>();

    @Override
    public List<Item> getItems() {
        log.info("Найдены вещи: {} ", items);
        return items;
    }

    @Override
    public List<Item> findByUserId(long userId) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner() == userId) {
                itemList.add(item);
            }
        }
        log.info("Найдены вещи: {} ", itemList);
        return itemList;
    }

    @Override
    public Item save(Item item) {
        item.setId(getId());
        items.add(item);
        log.info("Сохранена вещь: {} ", item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Item oldItem = findItem(item.getId());
        items.remove(oldItem);
        items.add(item);
        log.info("Обновлена вещь: {} ", item);
        return item;
    }

    @Override
    public Item findItem(long itemId) {
        Item newItem = null;
        for (Item item : items) {
            if (item.getId() == itemId) {
                newItem = item;
            }
        }
        log.info("Найдена вещь: {} ", newItem);
        return newItem;
    }

    @Override
    public void deleteByItemId(long itemId) {
        items.remove(findItem(itemId));
        log.info("Удалена вещь: {} ", findItem(itemId));
    }

    @Override
    public List<Item> searchByText(String text) {
        return getItems().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    private long getId() {
        long lastId = items
                .stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
