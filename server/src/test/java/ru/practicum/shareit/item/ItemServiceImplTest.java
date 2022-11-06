package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDtoToItem;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.StatusType.APPROVED;
import static ru.practicum.shareit.booking.StatusType.CANCELED;

public class ItemServiceImplTest {
    private ItemRepository itemRepository;
    private UserService userService;
    private BookingRepository bookingRepository;
    private BookingService bookingService;
    private CommentRepository commentRepository;
    private ItemRequestService itemRequestService;
    private ItemMapper itemMapper;
    private BookingMapper bookingMapper;
    private CommentMapper commentMapper;
    private MyPageRequest myPageRequest;
    private ItemServiceImpl itemService;

    private User user1;
    private User user2;
    private UserDto userDto;
    private Item item;
    private ItemDto itemDto;
    private ItemDtoWithBooking itemDtoWithBooking;
    private ItemDtoWithBooking itemDtoWithBooking1;
    private ItemRequest itemRequest1;
    private BookingDtoToItem lastDtoBooking;
    private BookingDtoToItem nextDtoBooking;
    private Booking lastBooking;
    private Booking nextBooking;
    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userService = mock(UserService.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = mock(BookingService.class);
        commentRepository = mock(CommentRepository.class);
        itemRequestService = mock(ItemRequestService.class);
        itemMapper = mock(ItemMapper.class);
        bookingMapper = mock(BookingMapper.class);
        commentMapper = mock(CommentMapper.class);
        myPageRequest = mock(MyPageRequest.class);
        itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, bookingService,
                commentRepository, itemRequestService, itemMapper, bookingMapper, commentMapper, myPageRequest);

        userDto = new UserDto(1L, "John", "john.doe@mail.com");
        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "User", "user@mail.com");
        item = new Item(1L, "Дрель", "дрель ударная", true, user1.getId(), null);
        itemDto = new ItemDto(1L, "Дрель", "дрель ударная", true, null);
        lastDtoBooking = new BookingDtoToItem(1L,
                LocalDateTime.of(2022, 1, 1, 12, 0),
                LocalDateTime.of(2022, 2, 1, 12, 0), 1L, 1L, CANCELED);
        nextDtoBooking = new BookingDtoToItem(2L,
                LocalDateTime.of(2023, 2, 1, 12, 0),
                LocalDateTime.of(2023, 12, 1, 12, 0), 1L, 1L, APPROVED);
        lastBooking = new Booking(1L,
                LocalDateTime.of(2022, 1, 1, 12, 0),
                LocalDateTime.of(2022, 2, 1, 12, 0), item, user1, CANCELED);
        nextBooking = new Booking(2L,
                LocalDateTime.of(2023, 2, 1, 12, 0),
                LocalDateTime.of(2023, 12, 1, 12, 0), item, user1, APPROVED);
        commentDto = new CommentDto(1L, "Удобная дрель", user1.getName(),
                LocalDateTime.of(2022, 3, 1, 12, 0));
        comment = new Comment(1L, "Удобная дрель", item, user1,
                LocalDateTime.of(2022, 3, 1, 12, 0));
        itemDtoWithBooking = ItemMapper.toDtoWithBooking(item, lastDtoBooking, nextDtoBooking,
                new ArrayList<>(Collections.singletonList(commentDto)));
        itemDtoWithBooking1 = ItemMapper.toDtoWithBooking(item, new ArrayList<>(Collections.singletonList(commentDto)));
        itemRequest1 = new ItemRequest(1L, "Дрель", user2,
                LocalDateTime.of(2022, 10, 1, 12, 0),
                new ArrayList<>());
    }

    @Test
    void findItemById() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        final Item newItem = itemService.findItemById(item.getId());
        Assertions.assertEquals(item, newItem);
        verify(itemRepository, times(1))
                .findById(item.getId());
    }

    @Test
    void findItemByWrongId() {
        when(itemRepository.findById(item.getId()))
                .thenThrow(new NotFoundException("Не найдена вещь с id = " + item.getId()));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.findItemById(item.getId()));
        Assertions.assertEquals("Не найдена вещь с id = " + item.getId(), exception.getMessage());
        verify(itemRepository, times(1))
                .findById(item.getId());
    }

    @Test
    void getItems() {
        when(itemRepository.findAllByOwnerOrderByIdAsc(anyLong(), any()))
                .thenReturn(new ArrayList<>(Collections.singletonList(item)));
        final List<ItemDtoWithBooking> items = itemService.getItems(user1.getId(), 0, 10);
        assertNotNull(items);
        assertEquals(1, items.size());
        verify(itemRepository, times(1))
                .findAllByOwnerOrderByIdAsc(anyLong(), any());
    }

    @Test
    void getLastBooking() {
        when(bookingRepository.findBookingByItemIdAndEndIsBefore(anyLong(), any()))
                .thenReturn(List.of(lastBooking));
        final BookingDtoToItem bookingDtoToItem = itemService.getLastBooking(item.getId());
        assertNotNull(bookingDtoToItem);
        assertEquals(lastDtoBooking, bookingDtoToItem);
        verify(bookingRepository, times(1))
                .findBookingByItemIdAndEndIsBefore(anyLong(), any());
    }

    @Test
    void getNextBooking() {
        when(bookingRepository.findBookingByItemIdAndStartIsAfter(anyLong(), any()))
                .thenReturn(new ArrayList<>(Collections.singletonList(nextBooking)));
        final BookingDtoToItem bookingDtoToItem = itemService.getNextBooking(item.getId());
        assertNotNull(bookingDtoToItem);
        assertEquals(nextDtoBooking, bookingDtoToItem);
        verify(bookingRepository, times(1))
                .findBookingByItemIdAndStartIsAfter(anyLong(), any());
    }

    @Test
    void getItem() {
        findItemById();
        when(bookingRepository.findBookingByItemIdAndEndIsBefore(anyLong(), any()))
                .thenReturn(new ArrayList<>(Collections.singletonList(lastBooking)));
        when(bookingRepository.findBookingByItemIdAndStartIsAfter(anyLong(), any()))
                .thenReturn(new ArrayList<>(Collections.singletonList(nextBooking)));
        when(commentRepository.findByItemIdOrderByCreatedDesc(item.getId()))
                .thenReturn(new ArrayList<>(Collections.singletonList(comment)));
        final ItemDtoWithBooking item1 = itemService.getItem(user1.getId(), item.getId());
        assertNotNull(item1);
        assertEquals(itemDtoWithBooking, item1);
        verify(bookingRepository, times(1))
                .findBookingByItemIdAndEndIsBefore(anyLong(), any());
        verify(bookingRepository, times(1))
                .findBookingByItemIdAndStartIsAfter(anyLong(), any());
        verify(commentRepository, times(1))
                .findByItemIdOrderByCreatedDesc(item.getId());
    }

    @Test
    void getItemWithOutBooking() {
        item.setOwner(2L);
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(item.getId()))
                .thenReturn(new ArrayList<>(Collections.singletonList(comment)));
        final ItemDtoWithBooking item1 = itemService.getItem(user1.getId(), item.getId());
        assertNotNull(item1);
        assertEquals(itemDtoWithBooking, item1);
        verify(itemRepository, times(1))
                .findById(item.getId());
        verify(commentRepository, times(1))
                .findByItemIdOrderByCreatedDesc(item.getId());
    }

    @Test
    void addNewItem() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(itemRepository.save(item))
                .thenReturn(item);
        final Item saveItem = itemService.addNewItem(user1.getId(), itemDto);
        assertNotNull(saveItem);
        assertEquals(item, saveItem);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(itemRepository, times(1))
                .save(item);
    }

    @Test
    void addNewItemException() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(itemRepository.save(item))
                .thenThrow(new ValidationException("Указаны не верные параметры вещи"));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addNewItem(user1.getId(), itemDto));
        Assertions.assertEquals("Указаны не верные параметры вещи", exception.getMessage());
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(itemRepository, times(1))
                .save(item);
    }

    @Test
    void addNewItemWithRequest() {
        item.setRequest(itemRequest1);
        itemDto.setRequestId(1L);
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(itemRequestService.findRequestById(itemDto.getRequestId()))
                .thenReturn(itemRequest1);
        when(itemRepository.save(item))
                .thenReturn(item);
        final Item saveItem = itemService.addNewItem(user1.getId(), itemDto);
        assertNotNull(saveItem);
        assertEquals(item, saveItem);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(itemRequestService, times(1))
                .findRequestById(itemDto.getRequestId());
        verify(itemRepository, times(1))
                .save(item);
    }

    @Test
    void patchItem() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        findItemById();
        when(itemRepository.save(item))
                .thenReturn(item);
        final Item saveItem = itemService.patchItem(user1.getId(), itemDto, item.getId());
        assertNotNull(saveItem);
        assertEquals(item, saveItem);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(itemRepository, times(1))
                .save(item);
    }

    @Test
    void patchItemExceptionOwner() {
        item.setOwner(2L);
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        findItemById();
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.patchItem(user1.getId(), itemDto, item.getId()));
        Assertions.assertEquals("Вещь не принадлежит пользователю", exception.getMessage());
        verify(userService, times(1))
                .findUserById(user1.getId());
    }

    @Test
    void patchItemException() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        findItemById();
        when(itemRepository.save(item))
                .thenThrow(new ValidationException("Указаны не верные параметры вещи"));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.patchItem(user1.getId(), itemDto, item.getId()));
        Assertions.assertEquals("Указаны не верные параметры вещи", exception.getMessage());
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(itemRepository, times(1))
                .save(item);
    }

    @Test
    void deleteItem() {
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        findItemById();
        itemService.deleteItem(1L, 1L);
        verify(userService, times(1))
                .findUserById(user1.getId());
    }

    @Test
    void searchByText() {
        when(itemRepository.searchByText(any(), any()))
                .thenReturn(List.of(item));
        final List<Item> items = itemService.searchByText("дрель", 0, 10);
        assertNotNull(items);
        assertEquals(item, items.get(0));
        verify(itemRepository, times(1))
                .searchByText(any(), any());
    }

    @Test
    void getItemComments() {
        when(commentRepository.findByItemIdOrderByCreatedDesc(item.getId()))
                .thenReturn(List.of(comment));
        final List<CommentDto> commentsDto = itemService.getItemComments(item.getId());
        assertNotNull(commentsDto);
        assertEquals(commentDto, commentsDto.get(0));
        verify(commentRepository, times(1))
                .findByItemIdOrderByCreatedDesc(item.getId());
    }

    @Test
    void addComment() {
        findItemById();
        when(userService.findUserById(user1.getId()))
                .thenReturn(user1);
        when(bookingService.getAllByBooker(user1.getId(), String.valueOf(State.PAST), 0, 10))
                .thenReturn(List.of(lastBooking));
        when(commentRepository.save(any()))
                .thenReturn(comment);
        final Comment saveComment = itemService.addComment(item.getId(), user1.getId(), "Удобная дрель");
        assertNotNull(saveComment);
        assertEquals(comment, saveComment);
        verify(userService, times(1))
                .findUserById(user1.getId());
        verify(bookingService, times(1))
                .getAllByBooker(user1.getId(), String.valueOf(State.PAST), 0, 10);
        verify(commentRepository, times(1))
                .save(any());
    }

}
