package com.drapson.springauthtutorial.application.exceptions;

public class InvalidLinkTokenException extends RuntimeException {
  public InvalidLinkTokenException(String message) {
    super(message);
  }
}
