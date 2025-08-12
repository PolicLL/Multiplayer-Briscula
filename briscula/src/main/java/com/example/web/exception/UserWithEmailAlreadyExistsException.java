package com.example.web.exception;

public class UserWithEmailAlreadyExistsException extends RuntimeException{
  public UserWithEmailAlreadyExistsException(String message) {
    super(message);
  }
}
