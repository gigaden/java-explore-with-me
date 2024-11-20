package ru.practicum.server.exception;

import lombok.Getter;

@Getter
public class StatisticValidationDateException extends RuntimeException {

    private final String reason = "For the requested operation the conditions are not met.";

    public StatisticValidationDateException(String message) {
        super(message);
    }
}