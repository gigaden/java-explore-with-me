package ru.practicum.statistics.exception;

public class ClientRequestException extends RuntimeException {
    public ClientRequestException(String message) {
        super(message);
    }
}