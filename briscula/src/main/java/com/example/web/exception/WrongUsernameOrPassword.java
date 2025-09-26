package com.example.web.exception;

public class WrongUsernameOrPassword extends RuntimeException {

    public WrongUsernameOrPassword() {
        super("Entered username or password is wrong.");
    }
}
