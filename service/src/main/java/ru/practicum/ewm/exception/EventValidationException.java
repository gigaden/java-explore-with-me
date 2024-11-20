package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class EventValidationException extends RuntimeException {

    private final String reason = "For the requested operation the conditions are not met.";

    public EventValidationException(String message) {
        super(message);
    }
}