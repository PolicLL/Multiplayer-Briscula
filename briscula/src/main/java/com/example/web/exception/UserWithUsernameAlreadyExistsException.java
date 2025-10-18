package com.example.web.exception;

public class UserWithUsernameAlreadyExistsException extends RuntimeException {
    public UserWithUsernameAlreadyExistsException(String name) {
        super(String.format("Username %s is already taken!", name));
    }

    public UserWithUsernameAlreadyExistsException() {
        super("Username is already taken!");
    }
}