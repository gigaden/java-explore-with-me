package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class CompilationNotFoundException extends RuntimeException {

    private final String reason = "The required object was not found.";

    public CompilationNotFoundException(String message) {
        super(message);
    }
}