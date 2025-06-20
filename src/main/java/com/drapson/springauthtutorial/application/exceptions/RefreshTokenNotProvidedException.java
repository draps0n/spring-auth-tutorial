package com.drapson.springauthtutorial.application.exceptions;

public class RefreshTokenNotProvidedException extends RuntimeException {
  public RefreshTokenNotProvidedException(String message) {
    super(message);
  }
}
