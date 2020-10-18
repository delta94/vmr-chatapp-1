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
  private Counter authCounter;
  private Counter rejectedCounter;

  @Inject
  public AuthInterceptor(JwtService jwtService, MeterRegistry meterRegistry) {
    this.jwtService = jwtService;
    authCounter = meterRegistry.counter("count_grpc_call", "status", "accepted");
    rejectedCounter = meterRegistry.counter("count_grpc_call", "status", "rejected");
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    // Get token from metadata
    Key<String> tokenKey = Key.of(TOKEN_HEADER_NAME, Metadata.ASCII_STRING_MARSHALLER);
    String token = headers.get(tokenKey);

    // Reject if token is null
    if (token == null) {
      rejectedCounter.increment();
      call.close(Status.UNAUTHENTICATED, headers);
      return new ServerCall.Listener<ReqT>() {};
    }

    try {
      // Verify token
      long userId = jwtService.authenticateBlocking(token);

      // Save user id to context
      Context context = Context.current().withValue(GrpcKey.USER_ID_KEY, String.valueOf(userId));
      authCounter.increment();

      // Pass to callback
      return Contexts.interceptCall(context, call, headers, next);
    } catch (Exception e) {
      // Jwt token not valid
      call.close(Status.UNAUTHENTICATED, headers);
      rejectedCounter.increment();
      return new ServerCall.Listener<ReqT>() {};
    }
  }
}
