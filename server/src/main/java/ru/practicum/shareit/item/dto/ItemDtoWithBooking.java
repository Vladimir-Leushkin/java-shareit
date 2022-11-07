package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoToItem;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ItemDtoWithBooking extends ItemDto {

    private BookingDtoToItem lastBooking;
    private BookingDtoToItem nextBooking;

    private List<CommentDto> comments;

    public ItemDtoWithBooking(Long id, String name, String description, Boolean available,
                              BookingDtoToItem lastBooking,
                              BookingDtoToItem nextBooking,
                              List<CommentDto> comments
    ) {
        super(id, name, description, available);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }

    public ItemDtoWithBooking(Long id, String name, String description, Boolean available,
                              List<CommentDto> comments
    ) {
        super(id, name, description, available);
        this.comments = comments;
    }
}
