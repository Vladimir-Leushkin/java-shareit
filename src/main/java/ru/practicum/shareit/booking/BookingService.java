package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoShort;

import java.util.List;

public interface BookingService {
    Booking addNewBooking(long userId, BookingDtoShort bookingDto);

    Booking patchBooking(long userId, Long bookingId, Boolean approved);

    List<Booking> getAllByBooker(Long userId, String state);

    List<Booking> getAllByOwner(Long userId, String state);

    Booking getById(Long userId, Long bookingId);

    Booking validateBooking(Long bookingId);
}
