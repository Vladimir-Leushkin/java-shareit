package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exeption.ValidationException;

public class MyPageRequest extends PageRequest {
    int from;

    protected MyPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    public static MyPageRequest createPageable(Integer from, Integer size, Sort sort) {
        if (from == null || size == null) {
            return null;
        } else {
            if (from < 0 || size <= 0) {
                throw new ValidationException("Указанные значения size/from меньше 0");
            }
            return new MyPageRequest(from, size, sort);
        }
    }

    @Override
    public long getOffset() {
        return from;
    }
}
