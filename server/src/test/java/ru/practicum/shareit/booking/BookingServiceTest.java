package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.MyPageRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.State.*;
import static ru.practicum.shareit.booking.StatusType.CANCELED;
import static ru.practicum.shareit.booking.StatusType.WAITING;

public class BookingServiceTest {
    private UserService userService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private BookingMapper bookingMapper;
    private BookingServiceImpl bookingService;

    private MyPageRequest myPageRequest;

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
        myPageRequest = mock(MyPageRequest.class);
        bookingService = new BookingServiceImpl(userService, bookingRepository, itemRepository, bookingMapper, myPageRequest);

        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "User", "user@mail.com");
        item1 = new Item(1L, "Дрель", "дрель ударная", true,
                user2.getId(), null);
        lastBooking = new Booking(1L,
                LocalDateTime.of(2022, 1, 1, 12, 0),
                LocalDateTime.of(2022, 2, 1, 12, 0), item1, user1, CANCELED);
        bookingDtoShort = new BookingDtoShort(1L,
                LocalDateTime.of(2023, 2,1,12,0),
                LocalDateTime.of(2023,12,1,12,0));
        nextBooking = new Booking(2L,
                LocalDateTime.of(2023, 2, 1, 12, 0),
                LocalDateTime.of(2023, 12, 1, 12, 0), item1, user1, WAITING);
    }

    @Test
    void findBookingById() {
        when(bookingRepository.findById(nextBooking.getId()))
                .thenReturn(Optional.ofNullable(nextBooking));
        final Booking booking = bookingService.findBookingById(nextBooking.getId());
        assertEquals(nextBooking, booking);
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
        assertEquals("Не найдено бронирование с id = " + lastBooking.getId(),
                exception.getMessage());
        verify(bookingRepository, times(1))
                .findById(lastBooking.getId());
    }

    @Test
    void findItemById() {
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        final Item item = bookingService.findItemById(item1.getId());
        assertEquals(item1, item);
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
        assertEquals(nextBooking, booking);
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
        assertEquals(nextBooking, booking);
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
        assertEquals(nextBooking, booking);
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
    void getAllByBookerWaiting() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(nextBooking));
        final List<Booking> booking = bookingService.getAllByBooker(user1.getId(),
                String.valueOf(WAITING), 0, 10);
        assertEquals(List.of(nextBooking), booking);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByBookerFuture() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(nextBooking));
        final List<Booking> booking = bookingService.getAllByBooker(user1.getId(),
                String.valueOf(FUTURE), 0, 10);
        assertEquals(List.of(nextBooking), booking);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByBookerPast() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(lastBooking));
        final List<Booking> booking = bookingService.getAllByBooker(user1.getId(),
                String.valueOf(PAST), 0, 10);
        assertEquals(List.of(lastBooking), booking);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByBookerCurrent() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(lastBooking));
        final List<Booking> booking = bookingService.getAllByBooker(user1.getId(),
                String.valueOf(CURRENT), 0, 10);
        assertEquals(new ArrayList<>(), booking);
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
        assertEquals("Ничего не найдено", exception.getMessage());
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
        assertEquals(List.of(nextBooking), booking);
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
        assertEquals(nextBooking, booking);
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
        assertEquals("Бронирование закрыто для пользователя", exception.getMessage());
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
        assertEquals("Вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void checkItemNotOwnerException() {
        item1.setOwner(user1.getId());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.checkItemNotOwner(item1, user1.getId()));
        assertEquals("Нельзя бронировать свои вещи", exception.getMessage());
    }

    @Test
    void checkItemOwnerException() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.checkItemOwner(item1, user1.getId()));
        assertEquals("Подтвердить бронирование может только собственник вещи", exception.getMessage());
    }

    @Test
    void checkActualTimeException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.checkActualTime(lastBooking));
        assertEquals("Неверно указано время", exception.getMessage());
    }

    @Test
    void checkBookingWaitingException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.checkBookingWaiting(lastBooking));
        assertEquals("Вещь уже забронирована", exception.getMessage());
    }

    @Test
    void checkApprovedFormatException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.checkApprovedFormat(null));
        assertEquals("Ошибка подтверждения", exception.getMessage());
    }

}
