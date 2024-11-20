package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class UserValidationException extends RuntimeException {

    private final String reason = "For the requested operation the conditions are not met.";

    public UserValidationException(String message) {
        super(message);
    }
}