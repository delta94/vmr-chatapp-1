package com.anhvan.vmr.grpc;

import com.anhvan.vmr.service.JwtService;
import io.grpc.*;
import io.grpc.Metadata.Key;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Log4j2
public class AuthInterceptor implements ServerInterceptor {
  public static final String TOKEN_HEADER_NAME = "x-jwt-token";

  private JwtService jwtService;
  private Counter counter;

  @Inject
  public AuthInterceptor(JwtService jwtService, MeterRegistry meterRegistry) {
    this.jwtService = jwtService;
    counter = meterRegistry.counter("count_grpc_call");
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    counter.increment();
    Key<String> jwtToken = Key.of(TOKEN_HEADER_NAME, Metadata.ASCII_STRING_MARSHALLER);
    String token = headers.get(jwtToken);

    if (token == null) {
      call.close(Status.UNAUTHENTICATED, headers);
      return new ServerCall.Listener<ReqT>() {};
    }

    try {
      long userId = jwtService.authenticateBlocking(token);
      Context context = Context.current().withValue(GrpcKey.USER_ID_KEY, String.valueOf(userId));
      return Contexts.interceptCall(context, call, headers, next);
    } catch (Exception e) {
      call.close(Status.UNAUTHENTICATED, headers);
      return new ServerCall.Listener<ReqT>() {};
    }
  }
}
