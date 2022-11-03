package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Item item3;

    @Autowired
    public ItemRepositoryTest(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "John", "john.doe@mail.com"));
        user2 = userRepository.save(new User(2L, "User", "user@mail.com"));
        item1 = itemRepository.save(new Item(1L, "Дрель", "дрель ударная", true,
                user1.getId(), null));
        item2 = itemRepository.save(new Item(2L, "дрель", "дрель ручная",
                true, user2.getId(), null));
        item3 = itemRepository.save(new Item(3L, "ножевка", "ножевка по металлу",
                true, user2.getId(), null));
    }

    @Test
    void findAllByOwnerOrderByIdAsc() {
        List<Item> byOwner = itemRepository.findAllByOwnerOrderByIdAsc(user2.getId(), PageRequest.ofSize(10));
        assertEquals(new ArrayList<>(Arrays.asList(item2, item3)), byOwner);
    }

    @Test
    void searchByText() {
        assertEquals(new ArrayList<>(Arrays.asList(item1, item2)),
                itemRepository.searchByText("дрель", PageRequest.ofSize(10)));
    }
}
