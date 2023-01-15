package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @GetMapping
    public List<ItemDtoWithBooking> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "from", defaultValue = "0")
                                             Integer from,
                                             @RequestParam(name = "size", defaultValue = "10")
                                             Integer size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.addNewItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody ItemDto itemDto,
                             @PathVariable Long itemId) {
        return ItemMapper.toItemDto(itemService.patchItem(userId, itemDto, itemId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text,
                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return ItemMapper.toItemsDto(itemService.searchByText(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable("itemId") Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody CommentDto text) {
        return CommentMapper.toDto(itemService.addComment(itemId, userId, text.getText()));
    }
}
