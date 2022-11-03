package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemRequestServiceImplTest {
    private UserService userService;
    private ItemRequestRepository itemRequestRepository;
    private ItemRequestMapper itemRequestMapper;
    private ItemRequestServiceImpl itemRequestService;

    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private ItemRequestDto itemRequestDto1;
    private ItemRequest itemRequest2;
    private MyPageRequest myPageRequest;

    @BeforeEach
    void beforeEach() {
        userService = mock(UserService.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestMapper = mock(ItemRequestMapper.class);
        myPageRequest = mock(MyPageRequest.class);
        itemRequestService = new ItemRequestServiceImpl(userService, itemRequestRepository, itemRequestMapper,
                myPageRequest);

        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "User", "user@mail.com");
        itemRequest1 = new ItemRequest(1L, "Дрель", user1, LocalDateTime.now(),
                new ArrayList<>());
        itemRequestDto1 = new ItemRequestDto(1L, "Дрель", user1, LocalDateTime.now());
        itemRequest2 = new ItemRequest(2L, "дрель ручная", user2,
                LocalDateTime.now(), new ArrayList<>());
    }

    @Test
    void addRequest() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest1);
        final ItemRequest saveItemRequest = itemRequestService.addRequest(user1.getId(), itemRequestDto1);
        assertNotNull(saveItemRequest);
        assertEquals(itemRequest1, saveItemRequest);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(itemRequestRepository, times(1))
                .save(any());
    }

    @Test
    void findAllRequests() {
        final PageImpl<ItemRequest> itemRequestPage = new PageImpl<>(Collections.singletonList(itemRequest2));
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(itemRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(itemRequestPage);
        Page<ItemRequest> itemRequestPage1 = itemRequestService.findAllRequests(user1.getId(),
                0, 10);
        assertNotNull(itemRequestPage1);
        assertEquals(itemRequestPage, itemRequestPage1);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdIsNotOrderByCreatedDesc(anyLong(), any());
    }

    @Test
    void findAllForRequestor() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(user1.getId()))
                .thenReturn(List.of(itemRequest1));
        List<ItemRequest> itemRequestList = itemRequestService.findAllForRequestor(user1.getId());
        assertNotNull(itemRequestList);
        assertEquals(List.of(itemRequest1), itemRequestList);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdOrderByCreatedDesc(user1.getId());
    }

    @Test
    void findRequestById() {
        when(itemRequestRepository.findById(itemRequest1.getId()))
                .thenReturn(Optional.ofNullable(itemRequest1));
        final ItemRequest saveItemRequest = itemRequestService.findRequestById(itemRequest1.getId());
        assertNotNull(saveItemRequest);
        assertEquals(itemRequest1, saveItemRequest);
        verify(itemRequestRepository, times(1))
                .findById(itemRequest1.getId());
    }

    @Test
    void findRequestByWrongId() {
        when(itemRequestRepository.findById(itemRequest1.getId()))
                .thenThrow(new NotFoundException("Не найден запрос с id = " + itemRequest1.getId()));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findRequestById(itemRequest1.getId()));
        Assertions.assertEquals("Не найден запрос с id = " + itemRequest1.getId(),
                exception.getMessage());
        verify(itemRequestRepository, times(1))
                .findById(itemRequest1.getId());
    }

    @Test
    void getRequest() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        findRequestById();
        final ItemRequest saveItemRequest = itemRequestService.getRequest(itemRequest1.getId(), user1.getId());
        assertNotNull(saveItemRequest);
        assertEquals(itemRequest1, saveItemRequest);
        verify(userService, times(1))
                .findUserById(user1.getId());
    }

}
