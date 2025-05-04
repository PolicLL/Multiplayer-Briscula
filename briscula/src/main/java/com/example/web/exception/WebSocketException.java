package com.example.web.exception;

public class WebSocketException extends RuntimeException{
  public WebSocketException() {
    super("Unsupported message.");
  }
}
