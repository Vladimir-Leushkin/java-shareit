package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


    @Override
    public Booking getById(Long userId, Long bookingId) {
        User user = userService.findUserById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id = " + bookingId));
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
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id = " + bookingDto.getItemId()));
        Booking booking = BookingMapper.shortDtoToBooking(item, user, bookingDto);
        if (!item.getIsAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Неверно указано время");
        }
        if (item.getOwner().equals(userId)) {
            throw new NotFoundException("Нельзя бронировать свои вещи");
        }
        Booking saveBooking = bookingRepository.save(booking);
        log.info("Пользователем id {}, подан запрос на бронирование вещи ({}), ", userId, item.getName());
        return saveBooking;

    }

    @Transactional
    @Override
    public Booking patchBooking(long userId, Long bookingId, Boolean approved) {
        userService.findUserById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id = " + bookingId));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id = " + booking.getItem().getId()));
        if (booking.getStatus() != StatusType.WAITING) {
            throw new ValidationException("Вещь уже забронирована");
        }
        if (approved == null) {
            throw new ValidationException("Ошибка подтверждения");
        }
        if (!item.getOwner().equals(userId)) {
            throw new NotFoundException("Подтвердить бронирование может только собственник вещи");
        }
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
    public List<Booking> getAllByBooker(Long userId, String state) {
        userService.findUserById(userId);
        List<Booking> bookings = bookingRepository.findAllByBooker_idOrderByStartDesc(userId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("Ничего не найдено");
        }
        List<Booking> bookingsState = getBookingsByState(state, bookings);
        log.info("Найдены бронирования {}, у пользователя ({}), ", bookingsState, userId);
        return bookingsState;
    }

    @Override
    public List<Booking> getAllByOwner(Long userId, String state) {
        userService.findUserById(userId);
        List<Booking> bookings = bookingRepository.findOwnerAll(userId);
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
}