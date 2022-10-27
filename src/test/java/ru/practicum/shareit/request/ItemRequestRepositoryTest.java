package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTest {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    @Autowired
    public ItemRequestRepositoryTest(UserRepository userRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "John", "john.doe@mail.com"));
        user2 = userRepository.save(new User(2L, "User", "user@mail.com"));
        itemRequest1 = itemRequestRepository.save(new ItemRequest(1L, "Дрель", user1, LocalDateTime.now(),
                new ArrayList<>()));
        itemRequest2 = itemRequestRepository.save(new ItemRequest(2L, "дрель ручная", user2,
                LocalDateTime.now(), new ArrayList<>()));
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
        List<ItemRequest> requestList = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(user1.getId());
        assertEquals(requestList, new ArrayList<>(List.of(itemRequest1)));
    }

    @Test
    void findAllByRequestorIdIsNotOrderByCreatedDesc() {
        List<ItemRequest> itemRequestPage = itemRequestRepository
                .findAllByRequestorIdIsNotOrderByCreatedDesc(user1.getId(), PageRequest.of(0, 1)).toList();
        assertEquals(itemRequestPage, new ArrayList<>(List.of(itemRequest2)));
    }
}
