package com.example.web.exception;

public class BadRequestException extends RuntimeException {
  public BadRequestException() {
    super("Either 'id' or 'username' must be provided.");
  }
}