package com.example.web.exception;

public class UserWithEmailAlreadyExistsException extends RuntimeException {
    public UserWithEmailAlreadyExistsException() {
        super("Email is already taken!");
    }
}
