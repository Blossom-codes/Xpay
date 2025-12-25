package com.dot.Xpay.exception;



import com.dot.Xpay.dto.response.BaseResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            // If multiple errors for same field, keep only the first (or join them)
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        });

        String combinedMessage = String.join(", ", errors.values());

        return new ResponseEntity<>(new BaseResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), combinedMessage,null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse> handleBadRequest(HttpMessageNotReadableException ex) {
        String message = "Malformed request or invalid enum value";
        log.error("An error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(new BaseResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), message, null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<BaseResponse> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("An error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(new BaseResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGenericException(Exception ex) {
        log.error("An error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(new BaseResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "Something went wrong, Please contact system administrators", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage(), ex);

        String message = "A data integrity error occurred.";

        Throwable rootCause = ex.getRootCause();
        if (rootCause != null) {
            String causeMessage = rootCause.getMessage().toLowerCase();

            if (causeMessage.contains("duplicate") || causeMessage.contains("unique")) {
                message = "This record already exists. Please use a different value.";
            } else if (causeMessage.contains("foreign key")) {
                message = "You are referencing a record that does not exist.";
            } else if (causeMessage.contains("cannot insert the value null")) {
                message = "A required field is missing.";
            }
        }

        BaseResponse error = new BaseResponse(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                message,
                null
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse> handleCustomException(CustomException ex) {
        log.error("An error occurred: {}", ex.getMessage(), ex);
        BaseResponse error = new BaseResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage(), null);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse> handleRunTimeException(RuntimeException ex) {
        log.error("An error occurred: {}", ex.getMessage(), ex);
        BaseResponse error = new BaseResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage(), null);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.error("An error occurred: {}", ex.getMessage(), ex);
        BaseResponse error = new BaseResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage(), null);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}
