package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class CommentValidationException extends RuntimeException {

    private final String reason = "For the requested operation the conditions are not met.";

    public CommentValidationException(String message) {
        super(message);
    }
}