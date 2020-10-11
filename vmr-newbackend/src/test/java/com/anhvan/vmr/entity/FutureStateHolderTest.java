package com.anhvan.vmr.entity;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Log4j2
public class FutureStateHolderTest {
  FutureStateHolder holder;

  @BeforeEach
  void setUp() {
    holder = new FutureStateHolder();
  }

  @Test
  void testGetState() {
    holder.set("msg", "Hello world");
    String msg = holder.get("msg");
    Assertions.assertEquals("Hello world", msg);
  }

  @Test
  void testInvalidState() {
    holder.set("msg", 199);
    Assertions.assertThrows(
        ClassCastException.class,
        () -> {
          String msg = holder.get("msg");
          log.debug(msg);
        });
  }
}
