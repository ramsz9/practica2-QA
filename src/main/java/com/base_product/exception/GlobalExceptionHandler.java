package com.base_product.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.base_product.dto.ErrorMessageDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorMessageDto> handleDuplicate(DuplicateResourceException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDto> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .sorted()
                .collect(Collectors.joining(", "));
        return buildResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessageDto> handleMalformedJson(HttpMessageNotReadableException ex) {
        return buildResponse("Malformed JSON request", HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorMessageDto> buildResponse(String message, HttpStatus status) {
        ErrorMessageDto error = new ErrorMessageDto(message, status, Instant.now().toEpochMilli());
        return new ResponseEntity<>(error, status);
    }

}
