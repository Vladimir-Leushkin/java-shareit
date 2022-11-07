package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final MyPageRequest myPageRequest;

    @Override
    public Booking getById(Long userId, Long bookingId) {
        User user = userService.findUserById(userId);
        Booking booking = findBookingById(bookingId);
        if (booking.getBooker().getId().equals(user.getId()) || booking.getItem().getOwner().equals(user.getId())) {
            log.info("Найдено бронирование ({}), ", booking);
            return booking;
        }
        throw new NotFoundException("Бронирование закрыто для пользователя");
    }

    @Transactional
    @Override
    public Booking addNewBooking(long userId, BookingDtoShort bookingDto) {
        User user = userService.findUserById(userId);
        Item item = findItemById(bookingDto.getItemId());
        Booking booking = BookingMapper.shortDtoToBooking(item, user, bookingDto);
        checkIsAvailableItem(item);
        checkItemNotOwner(item, userId);
        Booking saveBooking = bookingRepository.save(booking);
        log.info("Пользователем id {}, подан запрос на бронирование вещи ({}), ", userId, item.getName());
        return saveBooking;

    }

    @Transactional
    @Override
    public Booking patchBooking(long userId, Long bookingId, Boolean approved) {
        userService.findUserById(userId);
        Booking booking = findBookingById(bookingId);
        Item item = findItemById(booking.getItem().getId());
        checkBookingWaiting(booking);
        checkItemOwner(item, userId);
        if (approved) {
            booking.setStatus(StatusType.APPROVED);
        } else {
            booking.setStatus(StatusType.REJECTED);
        }
        Booking saveBooking = bookingRepository.save(booking);
        log.info("Одобрено новое бронирование ({}) вещи ({}), ", saveBooking.getId(), item.getName());
        return saveBooking;

    }

    @Override
    public List<Booking> getAllByBooker(Long userId, String state, Integer from, Integer size) {
        PageRequest pageRequest = myPageRequest.createPageable(from, size, Sort.unsorted());
        userService.findUserById(userId);
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
        if (bookings.isEmpty()) {
            throw new NotFoundException("Ничего не найдено");
        }
        List<Booking> bookingsState = getBookingsByState(state, bookings);
        log.info("Найдены бронирования {}, у пользователя ({}), ", bookingsState, userId);
        return bookingsState;
    }

    @Override
    public List<Booking> getAllByOwner(Long userId, String state, Integer from, Integer size) {
        PageRequest pageRequest = myPageRequest.createPageable(from, size, Sort.unsorted());
        userService.findUserById(userId);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(userId, pageRequest);
        List<Booking> bookingsState = getBookingsByState(state, bookings);
        log.info("Найдены бронирования вещей {}, пользователя ({}), ", bookingsState, userId);
        return bookingsState;
    }

    private List<Booking> getBookingsByState(String state, List<Booking> bookings) {
        switch (state.toUpperCase()) {
            case "ALL":
                return bookings;
            case "WAITING":
            case "REJECTED":
                StatusType status = StatusType.valueOf(state);
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(status))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isBefore(booking.getStart()))
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getStart())
                                && LocalDateTime.now().isBefore(booking.getEnd()))
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public Booking findBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id = " + bookingId));
        return booking;
    }

    protected Item findItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id = " + itemId));
        return item;
    }

    protected void checkIsAvailableItem(Item item) {
        if (!item.getIsAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
    }

    protected void checkItemNotOwner(Item item, long userId) {
        if (item.getOwner().equals(userId)) {
            throw new NotFoundException("Нельзя бронировать свои вещи");
        }
    }

    protected void checkItemOwner(Item item, long userId) {
        if (!item.getOwner().equals(userId)) {
            throw new NotFoundException("Подтвердить бронирование может только собственник вещи");
        }
    }

    protected void checkBookingWaiting(Booking booking) {
        if (booking.getStatus() != StatusType.WAITING) {
            throw new ValidationException("Вещь уже забронирована");
        }
    }

}
