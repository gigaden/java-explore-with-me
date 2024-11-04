package exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HashMap<String, String> handleNotFound(final NoSuchMethodError e, WebRequest request) {
        log.error("Ошибка 404 NotFoundException: {} в запросе {}",
                e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public HashMap<String, String> handleOtherExc(final Throwable e, WebRequest request) {
        log.error("Ошибка 500: {} в запросе {}", e.getMessage(),
                request.getDescription(false));
        return buildErrorResponse(e.getMessage());
    }

    public HashMap<String, String> buildErrorResponse(String message) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }


}