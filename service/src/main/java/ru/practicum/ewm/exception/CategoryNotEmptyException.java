package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class CategoryNotEmptyException extends RuntimeException {
    private final String reason ="For the requested operation the conditions are not met.";

    public CategoryNotEmptyException(String message) {
        super(message);
    }
}