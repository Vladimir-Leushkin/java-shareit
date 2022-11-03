package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.StatusType.CANCELED;
import static ru.practicum.shareit.booking.StatusType.WAITING;

@WebMvcTest(BookingController.class)
@Import(BookingMapper.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @MockBean
    private BookingServiceImpl bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private User user1;
    private User user2;
    private Item item1;
    private Booking lastBooking;
    private BookingDtoShort bookingDtoShort;
    private Booking nextBooking;
    private BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
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
        bookingDto = new BookingDto(2L,
                LocalDateTime.of(2022, 11, 1, 12, 0),
                LocalDateTime.of(2022, 12, 1, 12, 0), item1, user1, WAITING);
    }

    @Test
    void addBooking() throws Exception {
        when(bookingService.addNewBooking(anyLong(), any()))
                .thenReturn(nextBooking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoShort))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(nextBooking)));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getById(anyLong(), any()))
                .thenReturn(nextBooking);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void patchBooking() throws Exception {
        when(bookingService.patchBooking(anyLong(), anyLong(), any()))
                .thenReturn(nextBooking);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getAllByBooker() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(nextBooking));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAllByOwner() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(nextBooking));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }
}
