package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithBooking> getItems(long userId);

    ItemDtoWithBooking getItem(long userId, long itemId);

    Item addNewItem(long userId, ItemDto itemDto);

    Item patchItem(long userId, ItemDto itemDto, long itemId);

    void deleteItem(long userId, long itemId);

    List<Item> searchByText(String text);

    Comment addComment(Long itemId, long userId, String text);

    Item validateItem(Long itemId);
}
