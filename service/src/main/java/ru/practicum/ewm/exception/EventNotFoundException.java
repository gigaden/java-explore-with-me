package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class EventNotFoundException extends RuntimeException {
    private final String reason = "Not required object was not found.";

    public EventNotFoundException(String message) {
        super(message);
    }
}