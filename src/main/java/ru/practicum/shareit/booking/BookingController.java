package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("{bookingId}")
    public BookingDto getBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return BookingMapper.toBookingDto(bookingService.getById(userId, bookingId));
    }

    @PostMapping
    public BookingDto addBooking(
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId,
            @Valid @RequestBody BookingDtoShort bookingDto) {
        return BookingMapper.toBookingDto(bookingService.addNewBooking(userId, bookingDto));
    }

    @PatchMapping("{bookingId}")
    public BookingDto patchBooking(
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId,
            @PathVariable @NotNull Long bookingId,
            @PathParam("approved") @NotNull Boolean approved) {
        return BookingMapper.toBookingDto(bookingService.patchBooking(userId, bookingId, approved));
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        PageRequest pageRequest = MyPageRequest.createPageable(from, size, Sort.unsorted());
        return BookingMapper.toBookingsDto(bookingService.getAllByBooker(userId, state, pageRequest));
    }

    @GetMapping("owner")
    public List<BookingDto> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        PageRequest pageRequest = MyPageRequest.createPageable(from, size, Sort.unsorted());
        return BookingMapper.toBookingsDto(bookingService.getAllByOwner(userId, state, pageRequest));
    }
}
