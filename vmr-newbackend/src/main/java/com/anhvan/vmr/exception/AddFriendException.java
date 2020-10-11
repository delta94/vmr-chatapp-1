package com.anhvan.vmr.exception;

import lombok.Getter;

@Getter
public class AddFriendException extends RuntimeException {
  public enum ErrorCode {
    ACCEPTED,
    WAITING,
    NOT_ANSWER
  }

  private final ErrorCode errorCode;

  public AddFriendException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }
}
