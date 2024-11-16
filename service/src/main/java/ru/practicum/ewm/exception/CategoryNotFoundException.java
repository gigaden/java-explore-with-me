package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class CategoryNotFoundException extends RuntimeException {
    private final String reason ="The required object was not found.";

    public CategoryNotFoundException(String message) {
        super(message);
    }
}