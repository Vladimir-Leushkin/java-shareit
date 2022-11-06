package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GatewayErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GatewayErrorResponse handleInvalidException(final ValidationException e) {
        return new GatewayErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public GatewayErrorResponse handleNotFoundException(final NotFoundException e) {
        return new GatewayErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public GatewayErrorResponse handleConflictException(final ConflictException e) {
        return new GatewayErrorResponse(e.getMessage()
        );
    }
}
