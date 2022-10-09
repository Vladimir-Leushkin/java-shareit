package ru.practicum.shareit.booking.dto;

import com.sun.istack.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingDtoShort {
    @NotNull
    private Long ItemId;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
}
