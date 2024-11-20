package ru.practicum.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleValidation(final StatisticValidationException e, WebRequest request) {
        log.error("Ошибка 403 StatisticValidationException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.FORBIDDEN, e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(final StatisticValidationDateException e, WebRequest request) {
        log.error("Ошибка 400 StatisticValidationDateException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getReason());
    }

    public Map<String, String> buildErrorResponse(Exception e, HttpStatus status, String reason) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", status.name());
        response.put("reason", reason);
        response.put("message", e.getMessage());
        response.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return response;
    }


}