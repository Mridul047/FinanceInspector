package com.mycodethesaurus.financeinspector.exception.handler;

import com.mycodethesaurus.financeinspector.dto.ErrorResponse;
import com.mycodethesaurus.financeinspector.dto.ValidationError;
import com.mycodethesaurus.financeinspector.dto.ValidationErrorResponse;
import com.mycodethesaurus.financeinspector.exception.DuplicateResourceException;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class CustomGlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    log.error("Resource not found: {}", ex.getMessage());
    ErrorResponse error =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Resource Not Found")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateResource(
      DuplicateResourceException ex, HttpServletRequest request) {
    log.error("Duplicate resource: {}", ex.getMessage());
    ErrorResponse error =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error("Duplicate Resource")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    log.error("Validation error: {}", ex.getMessage());
    List<ValidationError> validationErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                error ->
                    ValidationError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
            .toList();

    ValidationErrorResponse response =
        ValidationErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Invalid input data")
            .path(request.getRequestURI())
            .validationErrors(validationErrors)
            .build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    log.error("Business rule violation: {}", ex.getMessage());
    ErrorResponse error =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Business Rule Violation")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex, HttpServletRequest request) {
    log.error("Unexpected error occurred: ", ex);
    ErrorResponse error =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .path(request.getRequestURI())
            .build();
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
