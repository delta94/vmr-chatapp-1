package com.anhvan.vmr.consts;

public enum ResponseCode {
  SUCCESS(200),
  USERNAME_EXISTED(1000),
  PASSWORD_INVALID(1001),
  USERNAME_NOT_EXISTED(1002),
  CREDENTIALS_NOTVALID(1003);

  public final int codeValue;

  ResponseCode(int codeValue) {
    this.codeValue = codeValue;
  }
}
