package com.anhvan.vmr.cache;

public class CacheKey {
  private static final String PREFIX = "ZALOPAY:EXAMPLE:";
  private static final String PREFIX_USER = PREFIX + "USERS:";

  public static String getUserKey(String userId) {
    return PREFIX_USER + userId;
  }
}
