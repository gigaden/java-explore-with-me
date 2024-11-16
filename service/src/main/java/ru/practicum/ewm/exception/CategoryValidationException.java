package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class CategoryValidationException extends RuntimeException {

    private final String reason = "Incorrectly made request.";

    public CategoryValidationException(String message) {
        super(message);
    }
}