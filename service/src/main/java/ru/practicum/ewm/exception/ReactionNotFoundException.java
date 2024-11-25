package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class ReactionNotFoundException extends RuntimeException {
    private final String reason = "Not required object was not found.";

    public ReactionNotFoundException(String message) {
        super(message);
    }
}