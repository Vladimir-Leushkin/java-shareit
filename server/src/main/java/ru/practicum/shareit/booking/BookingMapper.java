package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingDtoToItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static List<BookingDto> toBookingsDto(List<Booking> bookings) {
        List<BookingDto> bookingsDto = bookings.stream()
                .map(booking -> BookingMapper.toBookingDto(booking)).collect(Collectors.toList());
        return bookingsDto;
    }

    public static Booking shortDtoToBooking(Item item, User user, BookingDtoShort bookingDto) {
        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                StatusType.WAITING
        );
    }

    public static BookingDtoToItem toItemBooking(Booking booking) {
        return new BookingDtoToItem(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }
}
