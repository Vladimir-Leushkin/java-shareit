package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.StatusType.CANCELED;
import static ru.practicum.shareit.booking.StatusType.WAITING;

public class BookingServiceTest {
    private UserService userService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private BookingMapper bookingMapper;
    private BookingServiceImpl bookingService;

    private User user1;
    private User user2;
    private Item item1;
    private Booking lastBooking;
    private BookingDtoShort bookingDtoShort;
    private Booking nextBooking;


    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userService = mock(UserService.class);
        bookingRepository = mock(BookingRepository.class);
        bookingMapper = mock(BookingMapper.class);

        bookingService = new BookingServiceImpl(userService, bookingRepository, itemRepository, bookingMapper);

        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "User", "user@mail.com");
        item1 = new Item(1L, "Дрель", "дрель ударная", true,
                user2.getId(), null);
        lastBooking = new Booking(1L,
                LocalDateTime.of(2022, 1, 1, 12, 0),
                LocalDateTime.of(2022, 2, 1, 12, 0), item1, user1, CANCELED);
        bookingDtoShort = new BookingDtoShort(1L,
                LocalDateTime.of(2022, 11, 1, 12, 0),
                LocalDateTime.of(2022, 12, 1, 12, 0));
        nextBooking = new Booking(2L,
                LocalDateTime.of(2022, 11, 1, 12, 0),
                LocalDateTime.of(2022, 12, 1, 12, 0), item1, user1, WAITING);
    }

    @Test
    void findBookingById() {
        when(bookingRepository.findById(nextBooking.getId()))
                .thenReturn(Optional.ofNullable(nextBooking));
        final Booking booking = bookingService.findBookingById(nextBooking.getId());
        Assertions.assertEquals(nextBooking, booking);
        verify(bookingRepository, times(1))
                .findById(nextBooking.getId());
    }

    @Test
    void findBookingByWrongId() {
        when(bookingRepository.findById(lastBooking.getId()))
                .thenThrow(new NotFoundException("Не найдено бронирование с id = " + lastBooking.getId()));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBookingById(lastBooking.getId()));
        Assertions.assertEquals("Не найдено бронирование с id = " + lastBooking.getId(),
                exception.getMessage());
        verify(bookingRepository, times(1))
                .findById(lastBooking.getId());
    }

    @Test
    void findItemById() {
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        final Item item = bookingService.findItemById(item1.getId());
        Assertions.assertEquals(item1, item);
        verify(itemRepository, times(1))
                .findById(item1.getId());
    }

    @Test
    void addNewBooking() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        findItemById();
        when(bookingRepository.save(any()))
                .thenReturn(nextBooking);
        final Booking booking = bookingService.addNewBooking(user1.getId(), bookingDtoShort);
        Assertions.assertEquals(nextBooking, booking);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(itemRepository, times(2))
                .findById(item1.getId());
        verify(bookingRepository, times(1))
                .save(any());
    }

    @Test
    void patchBooking() {
        when(userService.findUserById(user2.getId()))
                .thenReturn(user2);
        findBookingById();
        findItemById();
        when(bookingRepository.save(any()))
                .thenReturn(nextBooking);
        final Booking booking = bookingService.patchBooking(user2.getId(), nextBooking.getId(), true);
        Assertions.assertEquals(nextBooking, booking);
        verify(userService, times(1))
                .findUserById(user2.getId());
        verify(bookingRepository, times(2))
                .findById(nextBooking.getId());
        verify(itemRepository, times(2))
                .findById(item1.getId());
        verify(bookingRepository, times(1))
                .save(any());
    }

    @Test
    void patchBookingRejected() {
        when(userService.findUserById(user2.getId()))
                .thenReturn(user2);
        findBookingById();
        findItemById();
        when(bookingRepository.save(any()))
                .thenReturn(nextBooking);
        final Booking booking = bookingService.patchBooking(user2.getId(), nextBooking.getId(), false);
        Assertions.assertEquals(nextBooking, booking);
        verify(userService, times(1))
                .findUserById(user2.getId());
        verify(bookingRepository, times(2))
                .findById(nextBooking.getId());
        verify(itemRepository, times(2))
                .findById(item1.getId());
        verify(bookingRepository, times(1))
                .save(any());
    }

    @Test
    void getAllByBooker() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(nextBooking));
        final List<Booking> booking = bookingService.getAllByBooker(user1.getId(),
                String.valueOf(WAITING), 0, 10);
        Assertions.assertEquals(List.of(nextBooking), booking);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByBookerException() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new ArrayList<>());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllByBooker(user1.getId(), String.valueOf(WAITING), 0, 10));
        Assertions.assertEquals("Ничего не найдено", exception.getMessage());
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByOwner() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(nextBooking));
        final List<Booking> booking = bookingService.getAllByOwner(user1.getId(),
                String.valueOf(WAITING), 0, 10);
        Assertions.assertEquals(List.of(nextBooking), booking);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(bookingRepository, times(1))
                .findAllByItemOwnerOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getById() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        findBookingById();
        final Booking booking = bookingService.getById(user1.getId(), nextBooking.getId());
        Assertions.assertEquals(nextBooking, booking);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(bookingRepository, times(2))
                .findById(nextBooking.getId());
    }

    @Test
    void getByIdException() {
        nextBooking.setBooker(user2);
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        findBookingById();
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(user1.getId(), nextBooking.getId()));
        Assertions.assertEquals("Бронирование закрыто для пользователя", exception.getMessage());
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(bookingRepository, times(2))
                .findById(nextBooking.getId());
    }

    @Test
    void checkIsAvailableItemException() {
        item1.setIsAvailable(false);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.checkIsAvailableItem(item1));
        Assertions.assertEquals("Вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void checkItemNotOwnerException() {
        item1.setOwner(user1.getId());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.checkItemNotOwner(item1, user1.getId()));
        Assertions.assertEquals("Нельзя бронировать свои вещи", exception.getMessage());
    }

    @Test
    void checkItemOwnerException() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.checkItemOwner(item1, user1.getId()));
        Assertions.assertEquals("Подтвердить бронирование может только собственник вещи", exception.getMessage());
    }

    @Test
    void checkActualTimeException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.checkActualTime(lastBooking));
        Assertions.assertEquals("Неверно указано время", exception.getMessage());
    }

    @Test
    void checkBookingWaitingException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.checkBookingWaiting(lastBooking));
        Assertions.assertEquals("Вещь уже забронирована", exception.getMessage());
    }

    @Test
    void checkApprovedFormatException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.checkApprovedFormat(null));
        Assertions.assertEquals("Ошибка подтверждения", exception.getMessage());
    }

    @Test
    void createPageableException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.createPageable(-1, -1, Sort.unsorted()));
        Assertions.assertEquals("Указанные значения size/from меньше 0", exception.getMessage());
    }

    @Test
    void createPageable() {
        PageRequest page = PageRequest.of(0, 10);
        PageRequest page1 = bookingService.createPageable(0, 10, Sort.unsorted());
        Assertions.assertEquals(page, page1);
    }
}
