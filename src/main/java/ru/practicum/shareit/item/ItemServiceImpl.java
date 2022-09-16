package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> getItems(long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Item> items = itemRepository.findByUserId(userId);
        itemsDto = items.stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
        return itemsDto;
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        userService.checkUserById(userId);
        Item item = itemRepository.findItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        userService.checkUserById(userId);
        validateCreateItemDto(itemDto);
        Item item = ItemMapper.toItem(userId, itemDto);
        item.setOwner(userId);
        Item saveItem = itemRepository.save(item);
        return ItemMapper.toItemDto(saveItem);
    }

    @Override
    public ItemDto patchItem(long userId, ItemDto itemDto, long itemId) {
        userService.checkUserById(userId);
        checkItem(userId, itemId);
        Item item = itemRepository.findItem(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.update(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        userService.checkUserById(userId);
        checkItem(userId, itemId);
        itemRepository.deleteByItemId(itemId);
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        List<ItemDto> itemsDto = new ArrayList<>();
        if (text != null && !text.isBlank()) {
            List<Item> items = itemRepository.searchByText(text.toLowerCase());
            itemsDto = items.stream()
                    .map(item -> ItemMapper.toItemDto(item))
                    .collect(Collectors.toList());
        }
        return itemsDto;
    }

    protected void checkItem(long userId, long itemId) {
        List<Item> items = itemRepository.findByUserId(userId);
        Map<Long, Item> itemsMap = new HashMap<>();
        for (Item item : items) {
            itemsMap.put(item.getId(), item);
        }
        if (!itemsMap.containsKey(itemId)) {
            throw new NotFoundException("Вещь не найдена");
        }
    }

    private boolean validateCreateItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty() || itemDto.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty() ||
                itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Доступность должна быть определена");
        }
        return true;
    }
}
