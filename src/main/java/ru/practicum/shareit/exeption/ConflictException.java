package ru.practicum.shareit.exeption;

public class ConflictException extends RuntimeException {
    public ConflictException(String e) {
        super(e);
    }
}
