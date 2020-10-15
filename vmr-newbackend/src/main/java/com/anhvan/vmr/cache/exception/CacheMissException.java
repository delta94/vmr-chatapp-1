package com.anhvan.vmr.cache.exception;

public class CacheMissException extends RuntimeException {
  public CacheMissException(String key) {
    super("Cache miss when get key: " + key);
  }
}
