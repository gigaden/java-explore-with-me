package ru.practicum.server.exception;

public class StatisticValidationException extends RuntimeException {
    public StatisticValidationException(String message) {
        super(message);
    }
}