package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Booking {
    @NonNull
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private StatusType status;
}
