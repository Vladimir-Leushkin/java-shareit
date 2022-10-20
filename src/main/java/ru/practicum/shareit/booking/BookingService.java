package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoShort;

import java.util.List;

public interface BookingService {
    Booking addNewBooking(long userId, BookingDtoShort bookingDto);

    Booking patchBooking(long userId, Long bookingId, Boolean approved);

    List<Booking> getAllByBooker(Long userId, String state, PageRequest pageRequest);

    List<Booking> getAllByOwner(Long userId, String state, PageRequest pageRequest);

    Booking getById(Long userId, Long bookingId);

}

