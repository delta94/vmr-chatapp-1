package com.anhvan.vmr.util;

import com.anhvan.vmr.model.ColName;
import io.vertx.sqlclient.Row;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class RowMapperUtil {
  private static final byte TRUE_BYTE = (byte) 1;

  public static <T> T mapRow(Row row, Class<T> classType) {
    T instance;

    try {
      // Create new instance
      instance = classType.getDeclaredConstructor().newInstance();

      // Get all fields in class
      List<Field> fieldList = getAllFields(classType);

      // Iterate over all fields
      for (Field field : fieldList) {
        field.setAccessible(true);

        // Get value from row
        Object value = row.getValue(getColName(field));

        if (value != null) {
          // Get field type and value type
          Class<?> fieldClass = field.getType();
          Class<?> valueClass = value.getClass();

          if (isBoolean(fieldClass) && value instanceof Byte) {
            // Get boolean value
            field.set(instance, byteToBoolean(value));
          } else if (fieldClass.equals(valueClass) || getWrapper(fieldClass).equals(valueClass)) {
            field.set(instance, value);
          }
        }
      }
    } catch (Exception exception) {
      log.error("Error when create instance of class {}", classType, exception);
      instance = null;
    }

    return instance;
  }

  private static Class<?> getWrapper(Class<?> type) {
    if (int.class.equals(type)) {
      return Integer.class;
    } else if (float.class.equals(type)) {
      return Float.class;
    } else if (long.class.equals(type)) {
      return Long.class;
    } else if (double.class.equals(type)) {
      return Double.class;
    } else if (char.class.equals(type)) {
      return Character.class;
    } else if (boolean.class.equals(type)) {
      return Boolean.class;
    } else if (short.class.equals(type)) {
      return Short.class;
    } else if (byte.class.equals(type)) {
      return Byte.class;
    }
    return type;
  }

  private static <T> List<Field> getAllFields(Class<T> classType) {
    List<Field> fieldList = new ArrayList<>(Arrays.asList(classType.getDeclaredFields()));
    Class<? super T> superClass = classType.getSuperclass();
    if (superClass != null) {
      fieldList.addAll(getAllFields(superClass));
    }
    return fieldList;
  }

  private static String getColName(Field field) {
    ColName colNameAnn = field.getAnnotation(ColName.class);
    if (colNameAnn != null) {
      return colNameAnn.value();
    }
    return field.getName();
  }

  private static <T> boolean isBoolean(Class<T> classType) {
    return classType == boolean.class || classType == Boolean.class;
  }

  private static Boolean byteToBoolean(Object object) {
    if (!(object instanceof Byte)) {
      return null;
    }
    return object.equals(TRUE_BYTE);
  }
}
