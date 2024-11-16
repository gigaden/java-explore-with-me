package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class RequestNotFoundException extends RuntimeException {

    private final String reason = "The required object was not found.";

    public RequestNotFoundException(String message) {
        super(message);
    }
}