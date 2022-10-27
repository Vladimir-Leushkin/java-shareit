package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequest addRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userService.findUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(user, itemRequestDto);
        ItemRequest saveItemRequest = itemRequestRepository.save(itemRequest);
        log.info("Пользователем id {}, подан запрос ({}), ", userId, itemRequest.getDescription());
        return saveItemRequest;
    }

    @Override
    public Page<ItemRequest> findAllRequests(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = createPageable(from, size, Sort.unsorted());
        User user = userService.findUserById(userId);
        Page<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorIdIsNotOrderByCreatedDesc(userId, pageRequest);
        log.info("Найдены запросы {} ", itemRequests);
        return itemRequests;
    }

    @Override
    public List<ItemRequest> findAllForRequestor(Long userId) {
        User user = userService.findUserById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        log.info("Найдены запросы {}, у пользователя ({}), ", itemRequests, userId);
        return itemRequests;
    }

    @Override
    public ItemRequest getRequest(long requestId, long userId) {
        User user = userService.findUserById(userId);
        ItemRequest itemRequest = findRequestById(requestId);
        log.info("Найден запрос {}, ", requestId);
        return itemRequest;
    }

    @Override
    public ItemRequest findRequestById(long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Не найден запрос с id = " + requestId));
        return itemRequest;
    }

    private PageRequest createPageable(Integer from, Integer size, Sort sort) {
        int page = from / size;
        if (from == null || size == null) {
            return null;
        } else {
            if (from < 0 || size <= 0) {
                throw new ValidationException("Указанные значения size/from меньше 0");
            }
        }
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return pageRequest;
    }
}
