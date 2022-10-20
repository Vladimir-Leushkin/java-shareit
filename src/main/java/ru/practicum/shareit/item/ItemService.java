package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithBooking> getItems(long userId, PageRequest pageRequest);

    ItemDtoWithBooking getItem(long userId, long itemId);

    Item addNewItem(long userId, ItemDto itemDto);

    Item patchItem(long userId, ItemDto itemDto, long itemId);

    void deleteItem(long userId, long itemId);

    List<Item> searchByText(String text, PageRequest pageRequest);

    Comment addComment(Long itemId, long userId, String text);
}
