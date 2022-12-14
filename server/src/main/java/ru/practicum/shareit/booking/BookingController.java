package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(bookingService.getById(userId, bookingId));
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody BookingDtoShort bookingDto) {
        return BookingMapper.toBookingDto(bookingService.addNewBooking(userId, bookingDto));
    }

    @PatchMapping("{bookingId}")
    public BookingDto patchBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @PathParam("approved") Boolean approved) {
        return BookingMapper.toBookingDto(bookingService.patchBooking(userId, bookingId, approved));
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return BookingMapper.toBookingsDto(bookingService.getAllByBooker(userId, state, from, size));
    }

    @GetMapping("owner")
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "ALL") String state,
                                          @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return BookingMapper.toBookingsDto(bookingService.getAllByOwner(userId, state, from, size));
    }
}
