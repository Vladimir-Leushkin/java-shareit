package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@Import(ItemRequestMapper.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @MockBean
    ItemRequestServiceImpl itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private User user1;
    private User user2;
    private ItemDto itemDto;
    private ItemRequestDtoWithItems itemRequestDtoWithItems1;
    private ItemRequest itemRequest1;
    private ItemRequestDto itemRequestDto1;
    private ItemRequest itemRequest2;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "User", "user@mail.com");
        itemRequest1 = new ItemRequest(1L, "Дрель", user1,
                LocalDateTime.of(2022, 11, 1, 12, 0),
                new ArrayList<>());
        itemDto = new ItemDto(1L, "Дрель", "дрель ударная", true, null);
        itemRequestDto1 = new ItemRequestDto(1L, "Дрель", user1,
                LocalDateTime.of(2022, 11, 1, 12, 0));
        itemRequestDtoWithItems1 = new ItemRequestDtoWithItems(1L, "Дрель", user1,
                LocalDateTime.of(2022, 11, 1, 12, 0), new ArrayList<>());
        itemRequest2 = new ItemRequest(2L, "дрель ручная", user2,
                LocalDateTime.now(), new ArrayList<>());
    }

    @Test
    void addRequest() throws Exception {
        when(itemRequestService.addRequest(user1.getId(), itemRequestDto1)).thenReturn(itemRequest1);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto1))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDto1)));

    }

    @Test
    void getItemRequest() throws Exception {
        when(itemRequestService.getRequest(itemRequestDto1.getId(), user1.getId()))
                .thenReturn(itemRequest1);

        mvc.perform(get("/requests/{requestId}", 1)
                        .content(mapper.writeValueAsString(itemRequestDto1))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDtoWithItems1)));
    }

    @Test
    void getAllRequest() throws Exception {
        final PageImpl<ItemRequest> itemRequestPage =
                new PageImpl<>(Collections.singletonList(itemRequest1));
        when(itemRequestService.findAllRequests(user2.getId(), 0, 10))
                .thenReturn(itemRequestPage);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "2")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDtoWithItems1))));
    }

    @Test
    void getAllForRequestor() throws Exception {
        when(itemRequestService.findAllForRequestor(1L))
                .thenReturn(List.of(itemRequest1));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDtoWithItems1))));
    }
}
