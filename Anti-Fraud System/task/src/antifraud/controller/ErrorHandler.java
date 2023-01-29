package antifraud.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler()
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ignoredE) {
        return ResponseEntity.badRequest().body(ignoredE.toString());
    }
}
