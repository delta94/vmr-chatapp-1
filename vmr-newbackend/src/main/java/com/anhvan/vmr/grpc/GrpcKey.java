package com.anhvan.vmr.grpc;

import io.grpc.Context;

public class GrpcKey {
  public static final Context.Key<String> USER_ID_KEY = Context.key("userId");

  public static long getUserId() {
    return Long.parseLong(USER_ID_KEY.get());
  }
}
