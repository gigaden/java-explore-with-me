package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class RequestValidationException extends RuntimeException {

    private final String reason = "Integrity constraint has been violated.";

    public RequestValidationException(String message) {
        super(message);
    }
}