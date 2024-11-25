package ru.practicum.ewm.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(final UserNotFoundException e, WebRequest request) {
        log.error("Ошибка 404 NotFoundException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleEventNotFound(final EventNotFoundException e, WebRequest request) {
        log.error("Ошибка 404 EventNotFoundException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCategoryNotFound(final CategoryNotFoundException e, WebRequest request) {
        log.error("Ошибка 404 CategoryNotFoundException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRequestNotFound(final RequestNotFoundException e, WebRequest request) {
        log.error("Ошибка 404 RequestNotFoundException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCompilationNotFound(final CompilationNotFoundException e, WebRequest request) {
        log.error("Ошибка 404 CompilationNotFoundException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleReactionNotFound(final ReactionNotFoundException e, WebRequest request) {
        log.error("Ошибка 404 ReactionNotFoundException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleCategoryValidation(final CategoryValidationException e, WebRequest request) {
        log.error("Ошибка 400 CategoryValidationException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.CONFLICT, e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleRequestValidation(final RequestValidationException e, WebRequest request) {
        log.error("Ошибка 409 RequestValidationException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.CONFLICT, e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleCategoryNotEmpty(final CategoryNotEmptyException e, WebRequest request) {
        log.error("Ошибка 409 CategoryNotEmptyException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.CONFLICT, e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEventValidation(final EventValidationException e, WebRequest request) {
        log.error("Ошибка 409 EventValidationException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.CONFLICT, e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleUserValidation(final UserValidationException e, WebRequest request) {
        log.error("Ошибка 409 UserValidationException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.CONFLICT, e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleReactionValidation(final ReactionValidationException e, WebRequest request) {
        log.error("Ошибка 409 ReactionValidationException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.CONFLICT, e.getReason());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class, NumberFormatException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidMethodArgument(Exception e, WebRequest request) {
        log.error("Ошибка 400 {}: {} в запросе {}",
                e.getClass(), e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, "Incorrectly made request.");
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