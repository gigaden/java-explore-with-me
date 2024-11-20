package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private final String reason = "The required object was not found.";

    public UserNotFoundException(String message) {
        super(message);
    }
}