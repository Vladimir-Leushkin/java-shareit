package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;


    @Override
    public List<ItemDtoWithBooking> getItems(long userId, PageRequest pageRequest) {
        List<Item> items = itemRepository.findAllByOwnerOrderByIdAsc(userId, pageRequest);
        log.info("Найден список вещей пользователя id ={}", userId);
        List<ItemDtoWithBooking> itemDtoWithBookings = items
                .stream()
                .map(item -> ItemMapper.toDtoWithBooking(item,
                        getLastBooking(item.getId()),
                        getNextBooking(item.getId()), getItemComments(item.getId()))).collect(Collectors.toList());
        return itemDtoWithBookings;
    }

    @Override
    public ItemDtoWithBooking getItem(long userId, long itemId) {
        Item item = findItemById(itemId);
        log.info("Найдена вещь с id ={}", itemId);
        if (item.getOwner() == userId) {
            BookingDtoToItem lastBooking = getLastBooking(itemId);
            BookingDtoToItem nextBooking = getNextBooking(itemId);
            List<CommentDto> comments = getItemComments(itemId);
            return ItemMapper.toDtoWithBooking(item, lastBooking, nextBooking, comments);
        } else {
            return ItemMapper.toDtoWithBooking(item, getItemComments(itemId));
        }
    }

    @Transactional
    @Override
    public Item addNewItem(long userId, ItemDto itemDto) {
        userService.findUserById(userId);
        Item item = ItemMapper.toItem(userId, itemDto);
        item.setOwner(userId);
        try {
            if (itemDto.getRequestId() != null) {
                ItemRequest itemRequest = itemRequestService.findRequestById(itemDto.getRequestId());
                item.setRequest(itemRequest);
                itemRequest.getItems().add(item);
            }
            Item saveItem = itemRepository.save(item);
            log.info("Пользователем id {}, добавлена вещь ({}), ", userId, item.getName());
            return saveItem;
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Указаны не верные параметры вещи");
        }
    }

    @Transactional
    @Override
    public Item patchItem(long userId, ItemDto itemDto, long itemId) {
        userService.findUserById(userId);
        Item item = findItemById(itemId);
        if (!item.getOwner().equals(userId)) {
            throw new NotFoundException("Вещь не принадлежит пользователю");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setIsAvailable(itemDto.getAvailable());
        }
        try {
            Item saveItem = itemRepository.save(item);
            log.info("Пользователем id {}, обновлена вещь ({}), ", userId, item.getName());
            return saveItem;
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Указаны не верные параметры вещи");
        }
    }

    @Transactional
    @Override
    public void deleteItem(long userId, long itemId) {
        userService.findUserById(userId);
        Item item = findItemById(itemId);
        if (item.getOwner().equals(userId)) {
            log.info("Пользователем id {}, удалена вещь ({}), ", userId, item.getName());
            itemRepository.deleteById(itemId);
        }
    }

    @Override
    public List<Item> searchByText(String text, PageRequest pageRequest) {
        List<Item> items = new ArrayList<>();
        if (text != null && !text.isBlank()) {
            items = itemRepository.searchByText(text.toLowerCase(), pageRequest);
            log.info("Найдены вещи ({}), ", items);
        }
        return items;
    }

    private BookingDtoToItem getNextBooking(long itemId) {
        Booking booking = bookingRepository.findBookingByItemIdAndStartIsAfter(itemId, LocalDateTime.now())
                .stream().min(Comparator.comparing(Booking::getStart)).orElse(null);
        if (booking == null) {
            return null;
        }
        return BookingMapper.toItemBooking(booking);
    }

    private BookingDtoToItem getLastBooking(long itemId) {
        Booking booking = bookingRepository.findBookingByItemIdAndEndIsBefore(itemId, LocalDateTime.now())
                .stream().max(Comparator.comparing(Booking::getEnd)).orElse(null);
        if (booking == null) {
            return null;
        }
        return BookingMapper.toItemBooking(booking);
    }

    private List<CommentDto> getItemComments(long itemId) {
        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);
        List<CommentDto> commentDto = comments
                .stream()
                .map(comment -> CommentMapper.toDto(comment))
                .collect(Collectors.toList());
        return commentDto;
    }

    @Transactional
    @Override
    public Comment addComment(Long itemId, long userId, String text) {
        Item item = findItemById(itemId);
        User user = userService.findUserById(userId);
        try {
            PageRequest pageRequest = PageRequest.of(0, 10, Sort.unsorted());
            bookingService.getAllByBooker(userId, String.valueOf(State.PAST), pageRequest)
                    .stream()
                    .filter(booking -> booking.getBooker().getId().equals(userId))
                    .findFirst().orElseThrow(() -> new NotFoundException("Пользователь не пользовался вещью"));
        } catch (NotFoundException e) {
            throw new ValidationException("Отзыв можно оставить только после использования вещи");
        }
        Comment comment = new Comment(null, text, item, user,
                LocalDateTime.now());
        log.info("Пользователем id {}, добавлена комментарий ({}), ", userId, comment);
        return commentRepository.save(comment);
    }

    private Item findItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id = " + itemId));
        return item;
    }
}
