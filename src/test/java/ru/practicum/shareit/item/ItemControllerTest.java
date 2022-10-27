package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoToItem;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.StatusType.APPROVED;
import static ru.practicum.shareit.booking.StatusType.CANCELED;

@WebMvcTest(ItemController.class)
@Import(ItemMapper.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    ItemServiceImpl itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private User user1;
    private User user2;
    private Item item1;
    private ItemDto itemDto1;
    private Item item2;
    private Item item3;
    private ItemDtoWithBooking itemDtoWithBooking;
    private ItemRequest itemRequest1;
    private BookingDtoToItem lastDtoBooking;
    private BookingDtoToItem nextDtoBooking;
    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "User", "user@mail.com");
        item1 = new Item(1L, "Дрель", "дрель ударная", true,
                user1.getId(), null);
        itemDto1 = new ItemDto(1L, "Дрель", "дрель ударная", true,
                null);
        item2 = new Item(2L, "дрель", "дрель ручная",
                true, user2.getId(), null);
        item3 = new Item(3L, "ножевка", "ножевка по металлу",
                true, user2.getId(), null);
        lastDtoBooking = new BookingDtoToItem(1L,
                LocalDateTime.of(2022, 1, 1, 12, 0),
                LocalDateTime.of(2022, 2, 1, 12, 0), 1L, 1L, CANCELED);
        nextDtoBooking = new BookingDtoToItem(1L,
                LocalDateTime.of(2022, 11, 1, 12, 0),
                LocalDateTime.of(2022, 12, 1, 12, 0), 1L, 1L, APPROVED);
        commentDto = new CommentDto(1L, "Удобная дрель", user1.getName(),
                LocalDateTime.of(2022, 3, 1, 12, 0));
        comment = new Comment(1L, "Удобная дрель", item1, user1,
                LocalDateTime.of(2022, 3, 1, 12, 0));
        itemDtoWithBooking = new ItemDtoWithBooking(1L, "Дрель", "дрель ударная", true,
                lastDtoBooking, nextDtoBooking, new ArrayList<>(Collections.singletonList(commentDto)));
        itemRequest1 = new ItemRequest(1L, "Дрель", user2,
                LocalDateTime.of(2022, 10, 1, 12, 0),
                new ArrayList<>());

    }

    @Test
    void addItem() throws Exception {
        when(itemService.addNewItem(user1.getId(), itemDto1)).thenReturn(item1);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto1)));
    }

    @Test
    void addItemWithRequest() throws Exception {
        item1.setRequest(itemRequest1);
        itemDto1.setRequestId(1L);
        when(itemService.addNewItem(user1.getId(), itemDto1)).thenReturn(item1);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto1)));
    }

    @Test
    void patchItem() throws Exception {
        when(itemService.patchItem(user1.getId(), itemDto1, itemDto1.getId())).thenReturn(item1);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto1))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto1)));
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItem(user1.getId(), item1.getId())).thenReturn(itemDtoWithBooking);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoWithBooking)));
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItems(1L, 0, 10))
                .thenReturn(List.of(itemDtoWithBooking));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDtoWithBooking))));
    }

    @Test
    void searchByText() throws Exception {
        when(itemService.searchByText(any(), anyInt(), anyInt())).thenReturn(List.of(item1));

        mvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto1))));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(item1.getId(), user1.getId(), commentDto.getText())).thenReturn(comment);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));
    }
}
