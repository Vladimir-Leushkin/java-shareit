package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(long userId);

    ItemDto getItem(long userId, long itemId);

    ItemDto addNewItem(long userId, ItemDto itemDto);

    ItemDto patchItem(long userId, ItemDto itemDto, long itemId);

    void deleteItem(long userId, long itemId);

    List<ItemDto> searchByText(String text);
}
