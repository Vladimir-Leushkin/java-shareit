package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.GatewayRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GatewayRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId,
            @Valid @RequestBody GatewayRequestDto requestDto) {
        log.info("Creating request {}, userId={}", requestDto, userId);
        return requestClient.addRequest(userId, requestDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequest(
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get requests userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAllRequest(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForRequestor(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get requests userId={}", userId);
        return requestClient.getAllForRequestor(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @PathVariable("requestId") long requestId,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get requestId={}, userId={}", requestId, userId);
        return requestClient.getItemRequest(requestId, userId);
    }
}
