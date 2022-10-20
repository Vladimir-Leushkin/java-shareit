package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.user.model.User;

import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(User user, ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getDescription(),
                user
        );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequestDtoWithItems toItemRequestWithItems(ItemRequest itemRequest) {
        return new ItemRequestDtoWithItems(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated(),
                itemRequest.getItems().stream()
                        .map(item -> ItemMapper.toItemDto(item))
                        .collect(Collectors.toList())
        );
    }
}
