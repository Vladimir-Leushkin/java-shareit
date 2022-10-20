package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return ItemRequestMapper.toItemRequestDto(itemRequestService.addRequest(userId, itemRequestDto));
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> getAllRequest(
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        PageRequest pageRequest = MyPageRequest.createPageable(from, size, Sort.unsorted());
        return itemRequestService.findAllRequests(userId, pageRequest)
                .stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestWithItems(itemRequest))
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> getAllForRequestor(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findAllForRequestor(userId)
                .stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestWithItems(itemRequest))
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItems getItemRequest(
            @PathVariable("requestId") long requestId,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return ItemRequestMapper.toItemRequestWithItems(itemRequestService.getRequest(requestId, userId));
    }
}
