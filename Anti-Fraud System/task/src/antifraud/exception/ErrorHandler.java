package antifraud.exception;

import antifraud.exception.InvalidIpException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<String> handleInvalidCard(ConstraintViolationException ignoredE) {
        return ResponseEntity.badRequest().body("Invalid card number!");
    }

    @ExceptionHandler(InvalidIpException.class)
    ResponseEntity<String> handleInvalidIpAddress(InvalidIpException ignoredE) {
        return ResponseEntity.badRequest().build();
    }
}
