package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.ValidationException;

@Component
public class MyPageRequest {

    public PageRequest createPageable(Integer from, Integer size, Sort sort) {
        if (from == null || size == null) {
            return null;
        } else {
            if (from < 0 || size <= 0) {
                throw new ValidationException("Указанные значения size/from меньше 0");
            }
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return pageRequest;
    }


}
