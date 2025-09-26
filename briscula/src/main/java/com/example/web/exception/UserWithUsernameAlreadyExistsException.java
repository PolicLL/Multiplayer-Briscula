package com.example.web.exception;

public class UserWithUsernameAlreadyExistsException extends RuntimeException {
    public UserWithUsernameAlreadyExistsException() {
        super("Username is already taken!");
    }
}