package com.example.web.exception;

public class UserWithUsernameAlreadyExistsException extends RuntimeException{
  public UserWithUsernameAlreadyExistsException(String message) {
    super(message);
  }
}