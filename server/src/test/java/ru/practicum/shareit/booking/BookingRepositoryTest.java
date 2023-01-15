package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.StatusType.APPROVED;
import static ru.practicum.shareit.booking.StatusType.CANCELED;

@DataJpaTest
public class BookingRepositoryTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Booking lastBooking;
    private Booking nextBooking;

    @Autowired
    public BookingRepositoryTest(UserRepository userRepository, ItemRepository itemRepository,
                                 BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "John", "john.doe@mail.com"));
        user2 = userRepository.save(new User(2L, "User", "user@mail.com"));
        item1 = itemRepository.save(new Item(1L, "Дрель", "дрель ударная", true,
                user2.getId(), null));
        lastBooking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().minusMonths(2),
                LocalDateTime.now().minusMonths(1), item1, user1, CANCELED));
        nextBooking = bookingRepository.save(new Booking(2L,
                LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusMonths(2), item1, user1, APPROVED));
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        List<Booking> bookingList = bookingRepository
                .findAllByBookerIdOrderByStartDesc(user1.getId(), PageRequest.of(0, 10));
        assertEquals(new ArrayList<>(Arrays.asList(nextBooking, lastBooking)), bookingList);
    }

    @Test
    void findBookingByItemIdAndEndIsBefore() {
        List<Booking> bookingList = bookingRepository
                .findBookingByItemIdAndEndIsBefore(item1.getId(), LocalDateTime.now());
        assertEquals(new ArrayList<>(Collections.singletonList(lastBooking)), bookingList);
    }

    @Test
    void findBookingByItemIdAndStartIsAfter() {
        List<Booking> bookingList = bookingRepository
                .findBookingByItemIdAndStartIsAfter(item1.getId(), LocalDateTime.now());
        assertEquals(new ArrayList<>(Collections.singletonList(nextBooking)), bookingList);
    }

    @Test
    void findAllByItemOwnerOrderByStartDesc() {
        List<Booking> bookingList = bookingRepository
                .findAllByItemOwnerOrderByStartDesc(user2.getId(), PageRequest.of(0, 10));
        assertEquals(new ArrayList<>(Arrays.asList(nextBooking, lastBooking)), bookingList);
    }
}
