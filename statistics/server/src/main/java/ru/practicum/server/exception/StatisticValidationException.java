package ru.practicum.server.exception;

import lombok.Getter;

@Getter
public class StatisticValidationException extends RuntimeException {

    private final String reason = "For the requested operation the conditions are not met.";

    public StatisticValidationException(String message) {
        super(message);
    }
}