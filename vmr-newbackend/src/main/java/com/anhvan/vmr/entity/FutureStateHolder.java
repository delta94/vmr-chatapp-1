package com.anhvan.vmr.entity;

import lombok.Builder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FutureStateHolder {
  @Builder.Default private Map<String, Object> objectMap = new ConcurrentHashMap<>();

  public void set(String key, Object value) {
    objectMap.put(key, value);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    return (T) objectMap.get(key);
  }
}
