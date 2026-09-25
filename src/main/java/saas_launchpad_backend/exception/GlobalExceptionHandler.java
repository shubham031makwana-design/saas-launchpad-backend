package saas_launchpad_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsageLimitExceededException.class)
    public ResponseEntity<String> handleUsageLimitExceeded(
            UsageLimitExceededException exception) {

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(exception.getMessage());
    }
}