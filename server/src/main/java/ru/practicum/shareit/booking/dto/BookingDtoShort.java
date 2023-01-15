package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingDtoShort {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
