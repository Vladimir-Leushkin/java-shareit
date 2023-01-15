package ru.practicum.shareit.exception;

import lombok.Value;

@Value
public class GatewayErrorResponse {
    private final String error;

    public GatewayErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
