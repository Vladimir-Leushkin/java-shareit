package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemRequestDtoWithItems {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemDto> items = new ArrayList<>();

}
