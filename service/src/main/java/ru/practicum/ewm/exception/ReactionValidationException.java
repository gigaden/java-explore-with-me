package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class ReactionValidationException extends RuntimeException {

    private final String reason = "For the requested operation the conditions are not met.";

    public ReactionValidationException(String message) {
        super(message);
    }
}