package com.anhvan.vmr.util;

import com.anhvan.vmr.model.User;
import io.vertx.sqlclient.Row;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@Log4j2
public class RowMapperUtilTest {
  @Test
  void testMapperOnUserObject() {
    Row row = Mockito.mock(Row.class);

    Mockito.when(row.getValue("id")).thenReturn(1L);
    Mockito.when(row.getValue("username")).thenReturn("danganhvan");
    Mockito.when(row.getValue("password")).thenReturn("12345678");

    User user = RowMapperUtil.mapRow(row, User.class);

    Assertions.assertEquals(1, user.getId());
    Assertions.assertEquals("danganhvan", user.getUsername());
    Assertions.assertEquals("12345678", user.getPassword());
  }

  @Test
  void testMapperInvalidOnUserObject() {
    // Create mock object
    Row row = Mockito.mock(Row.class);

    Mockito.when(row.getValue("id")).thenReturn(1);
    Mockito.when(row.getValue("username")).thenReturn("danganhvan");
    Mockito.when(row.getValue("password")).thenReturn("12345678");
    Mockito.when(row.getValue("is_active")).thenReturn("Sample");
    Mockito.when(row.getValue("name")).thenReturn("Dang Anh Van");

    User user = RowMapperUtil.mapRow(row, User.class);
    log.debug(user);
  }
}
