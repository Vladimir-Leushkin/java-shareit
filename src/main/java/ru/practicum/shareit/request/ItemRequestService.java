package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addRequest(long userId, ItemRequestDto itemRequestDto);

    Page<ItemRequest> findAllRequests(Long userId,  Integer from, Integer size);

    List<ItemRequest> findAllForRequestor(Long userId);

    ItemRequest getRequest(long requestId, long userId);

    ItemRequest findRequestById(long requestId);
}
