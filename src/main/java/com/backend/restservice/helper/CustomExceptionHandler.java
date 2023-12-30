package com.backend.restservice.helper;

import static com.backend.restservice.constant.Constants.ERROR_KEY;
import static com.backend.restservice.constant.Constants.INVALID_REQUEST_BODY;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.backend.restservice.exception.SessionNotFoundException;

import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorMap.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_KEY, INVALID_REQUEST_BODY);

        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSessionNotFoundException(SessionNotFoundException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_KEY, ex.getMessage());

        return new ResponseEntity<>(errorMap, HttpStatus.NOT_FOUND);
    }
}
