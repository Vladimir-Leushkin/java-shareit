package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getItems();

    List<Item> findByUserId(long userId);

    Item save(Item item);

    Item update(Item item);

    Item findItem(long itemId);

    void deleteByItemId(long itemId);

    List<Item> searchByText(String text);
}
