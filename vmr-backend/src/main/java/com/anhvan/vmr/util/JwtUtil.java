package com.anhvan.vmr.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JwtUtil {
  private final Algorithm algorithm;

  public JwtUtil(String privateKey) {
    algorithm = HMAC512(privateKey);
  }

  public String genKey(int userId) {
    return JWT.create()
        .withSubject(String.valueOf(userId))
        .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000 * 24 * 7))
        .sign(algorithm);
  }
}
