package com.example.web.exception;

import jakarta.persistence.EntityNotFoundException;

public class MatchNotFoundException extends EntityNotFoundException {
  public MatchNotFoundException(String id) {
    super("Match not found with id: " + id);
  }
}
