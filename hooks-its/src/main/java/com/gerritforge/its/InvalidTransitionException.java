package com.gerritforge.its;

import java.io.IOException;

public class InvalidTransitionException extends IOException {

  private static final long serialVersionUID = 1L;

  public InvalidTransitionException(String message) {
    super(message);
  }
}
