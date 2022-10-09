package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable()
        );
    }

    public static List<ItemDto> toItemsDto(List<Item> items) {
        List itemsDto = items.stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
        return itemsDto;
    }

    public static Item toItem(long userId, ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId
        );
    }

    public static ItemDtoWithBooking toDtoWithBooking(Item item, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }

    public static ItemDtoWithBooking toDtoWithBooking(Item item, List<CommentDto> comments) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                comments);
    }

}
