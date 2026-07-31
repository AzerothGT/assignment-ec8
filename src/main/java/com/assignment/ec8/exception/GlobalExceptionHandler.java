package com.assignment.ec8.exception;

import com.assignment.ec8.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message(errorMessage)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(jakarta.validation.ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(jakarta.validation.ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message(errorMessage)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.name())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.name())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.name())
                .message("Akses ditolak. Anda tidak memiliki role yang cukup")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .message(ex.getMessage() != null ? ex.getMessage() : "Terjadi kesalahan pada server")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
