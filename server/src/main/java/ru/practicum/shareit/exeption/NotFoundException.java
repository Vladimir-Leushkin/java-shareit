package ru.practicum.shareit.exeption;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String e) {
        super(e);
    }
}
