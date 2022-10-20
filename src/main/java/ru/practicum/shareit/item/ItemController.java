package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithBooking> getItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        PageRequest pageRequest = MyPageRequest.createPageable(from, size, Sort.unsorted());
        return itemService.getItems(userId, pageRequest);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.addNewItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto,
                             @PathVariable long itemId) {
        return ItemMapper.toItemDto(itemService.patchItem(userId, itemDto, itemId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(
            @RequestParam String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        PageRequest pageRequest = MyPageRequest.createPageable(from, size, Sort.unsorted());
        return ItemMapper.toItemsDto(itemService.searchByText(text, pageRequest));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable("itemId") Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody CommentDto text) {
        return CommentMapper.toDto(itemService.addComment(itemId, userId, text.getText()));
    }
}
