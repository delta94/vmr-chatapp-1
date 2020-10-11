package com.anhvan.vmr.exception;

import lombok.Getter;

@Getter
public class TransferException extends RuntimeException {
  public enum ErrorCode {
    RECEIVER_INVALID,
    BALANCE_NOT_ENOUGHT,
    REQUEST_EXISTED,
    PASSWORD_INVALID
  }

  private final ErrorCode errorCode;

  public TransferException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }
}
