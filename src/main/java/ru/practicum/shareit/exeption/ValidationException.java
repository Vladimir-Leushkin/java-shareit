package ru.practicum.shareit.exeption;

public class ValidationException extends RuntimeException {
    public ValidationException(String e) {
        super(e);
    }
}
