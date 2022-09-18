package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private ItemRequest request;


    public Item(Long id, String name, String description, Boolean available, long userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = userId;
    }
}
