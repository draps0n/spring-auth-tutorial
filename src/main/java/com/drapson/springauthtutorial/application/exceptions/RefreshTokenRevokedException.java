package com.drapson.springauthtutorial.application.exceptions;

public class RefreshTokenRevokedException extends RuntimeException {
  public RefreshTokenRevokedException(String message) {
    super(message);
  }
}
