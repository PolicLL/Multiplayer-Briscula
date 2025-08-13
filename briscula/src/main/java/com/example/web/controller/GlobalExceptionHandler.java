package com.example.web.controller;

import com.example.web.dto.ErrorResponse;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.TooBigNumberOfBotsException;
import com.example.web.exception.UserAlreadyAssignedToTournament;
import com.example.web.exception.UserAlreadyLoggedInException;
import com.example.web.exception.UserIsAlreadyInTournamentOrGame;
import com.example.web.exception.UserWithEmailAlreadyExistsException;
import com.example.web.exception.UserWithUsernameAlreadyExistsException;
import com.example.web.exception.WrongUsernameOrPassword;
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

  @ExceptionHandler(UserWithEmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserWithEmailAlreadyExistsException(
      UserWithEmailAlreadyExistsException e, HttpServletRequest request) {
    log.error("User already exists: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "User Already Exists With Email",
            e.getMessage(),
            request.getRequestURI()
        )
    );
  }

  @ExceptionHandler(UserWithUsernameAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserWithUsernameAlreadyExistsException(
      UserWithUsernameAlreadyExistsException e, HttpServletRequest request) {
    log.error("User already exists: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "User Already Exists With Username",
            e.getMessage(),
            request.getRequestURI()
        )
    );
  }

  @ExceptionHandler(UserAlreadyLoggedInException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyLoggedInException(UserAlreadyLoggedInException e, HttpServletRequest request) {
    log.error("User is already logged in: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            "User is already logged in",
            e.getMessage(),
            request.getRequestURI()
        )
    );
  }

  @ExceptionHandler(WrongUsernameOrPassword.class)
  public ResponseEntity<ErrorResponse> handleWrongUsernameOrPassword(WrongUsernameOrPassword e, HttpServletRequest request) {
    log.error("Wrong username or password.");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            "Incorrect username or password.",
            e.getMessage(),
            request.getRequestURI()
        )
    );
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad request: both id and username are null",
            e.getMessage(),
            request.getRequestURI()
        )
    );
  }

  @ExceptionHandler(TooBigNumberOfBotsException.class)
  public ResponseEntity<ErrorResponse> handleTooBigNumberOfBotsException(
      TooBigNumberOfBotsException e, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad request: Too big number of bots.",
            e.getMessage(),
            request.getRequestURI()
        )
    );
  }

  @ExceptionHandler(UserIsAlreadyInTournamentOrGame.class)
  public ResponseEntity<ErrorResponse> handleUserIsAlreadyInTournamentOrGame(
      TooBigNumberOfBotsException e, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "User is already in tournament or game.",
            e.getMessage(),
            request.getRequestURI()
        )
    );
  }
}
