package com.example.web.exception;

import jakarta.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String id) {
        super("User not found with id: " + id);
    }

    public UserNotFoundException() {
        super("User not found.");
    }
}
