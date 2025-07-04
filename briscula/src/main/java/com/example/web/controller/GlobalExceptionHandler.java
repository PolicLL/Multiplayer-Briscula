package com.example.web.controller;

import com.example.web.dto.ErrorResponse;
import com.example.web.exception.UserAlreadyAssignedToTournament;
import com.example.web.exception.UserAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
    String message = ex.getBindingResult().getAllErrors().stream()
        .map(ObjectError::getDefaultMessage)
        .collect(Collectors.joining("; "));

    return new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Validation Error",
        message,
        request.getRequestURI()
    );
  }

  @ExceptionHandler(UserAlreadyAssignedToTournament.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleUserAlreadyAssignedToTournament(UserAlreadyAssignedToTournament ex,  HttpServletRequest request) {
    return new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Validation Error",
        ex.getMessage(),
        request.getRequestURI()
    );
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
    log.error("Bad request: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            e.getMessage(),
            request.getRequestURI()
        )
    );
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
    log.error("Entity not found: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Entity Not Found",
            e.getMessage(),
            request.getRequestURI()
        )
    );
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e, HttpServletRequest request) {
    log.error("Entity already exists: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "User Already Exists",
            e.getMessage(),
            request.getRequestURI()
        )
    );
  }
}
