package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingDtoToItem;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithBooking> getItems(long userId, Integer from, Integer size);

    ItemDtoWithBooking getItem(long userId, long itemId);

    Item addNewItem(long userId, ItemDto itemDto);

    Item patchItem(long userId, ItemDto itemDto, long itemId);

    void deleteItem(long userId, long itemId);

    List<Item> searchByText(String text, Integer from, Integer size);

    BookingDtoToItem getNextBooking(long itemId);

    BookingDtoToItem getLastBooking(long itemId);

    List<CommentDto> getItemComments(long itemId);

    Comment addComment(Long itemId, long userId, String text);

    Item findItemById(Long itemId);
}
