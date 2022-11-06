package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exeption.ValidationException;

import static org.junit.jupiter.api.Assertions.assertNull;

public class MyPageRequestTest {
    private final MyPageRequest myPageRequest = new MyPageRequest();

    @Test
    void createPageableException1() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> myPageRequest.createPageable(0, -1, Sort.unsorted()));
        Assertions.assertEquals("Указанные значения size/from меньше 0", exception.getMessage());
    }

    @Test
    void createPageableException2() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> myPageRequest.createPageable(-1, 10, Sort.unsorted()));
        Assertions.assertEquals("Указанные значения size/from меньше 0", exception.getMessage());
    }

    @Test
    void createPageable() {
        PageRequest page = PageRequest.of(0, 10);
        PageRequest page1 = myPageRequest.createPageable(0, 10, Sort.unsorted());
        Assertions.assertEquals(page, page1);
    }

    @Test
    void createPageableNull1() {
        assertNull(myPageRequest.createPageable(0, null, Sort.unsorted()));

    }

    @Test
    void createPageableNull2() {
        assertNull(myPageRequest.createPageable(null, 10, Sort.unsorted()));

    }
}
