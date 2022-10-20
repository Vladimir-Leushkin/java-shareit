package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addRequest(long userId, ItemRequestDto itemRequestDto);

    Page<ItemRequest> findAllRequests(Long userId, PageRequest pageRequest);

    List<ItemRequest> findAllForRequestor(Long userId);

    ItemRequest getRequest(long requestId, long userId);

    ItemRequest findRequestById(long requestId);
}
