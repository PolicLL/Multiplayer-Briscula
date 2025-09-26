package com.example.web.exception;

public class UserAlreadyLoggedInException extends RuntimeException {

    public UserAlreadyLoggedInException(String name) {
        super(String.format("User with name %s is already logged in.", name));
    }
}