package com.anhvan.vmr.util;

import com.anhvan.vmr.model.User;
import io.vertx.sqlclient.Row;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class RowMapperUtilTest {
  @Test
  void testMapperOnUserObject() {
    Row row = Mockito.mock(Row.class);

    Mockito.when(row.getValue("id")).thenReturn(1);
    Mockito.when(row.getValue("username")).thenReturn("danganhvan");
    Mockito.when(row.getValue("password")).thenReturn("12345678");
    Mockito.when(row.getValue("is_active")).thenReturn(Byte.parseByte("1"));

    User user = RowMapperUtil.mapRow(row, User.class);

    Assertions.assertEquals(1, user.getId());
    Assertions.assertEquals("danganhvan", user.getUsername());
    Assertions.assertEquals("12345678", user.getPassword());
    Assertions.assertTrue(user.getActive());
  }

  @Test
  void testMapperInvalidOnUserObject() {
    // Create mock object
    Row row = Mockito.mock(Row.class);

    Mockito.when(row.getValue("id")).thenReturn(1);
    Mockito.when(row.getValue("username")).thenReturn("danganhvan");
    Mockito.when(row.getValue("password")).thenReturn("12345678");
    Mockito.when(row.getValue("is_active")).thenReturn("Sample");

    User user = RowMapperUtil.mapRow(row, User.class);

    Assertions.assertNull(user.getActive());
  }
}
