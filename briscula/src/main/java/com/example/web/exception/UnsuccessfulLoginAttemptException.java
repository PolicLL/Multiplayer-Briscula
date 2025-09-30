package com.example.web.exception;

public class UnsuccessfulLoginAttemptException extends RuntimeException {
    public UnsuccessfulLoginAttemptException() {
        super("Entered username or password is wrong.");
    }
}