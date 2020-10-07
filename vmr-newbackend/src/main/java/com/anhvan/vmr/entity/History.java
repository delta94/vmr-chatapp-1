package com.anhvan.vmr.entity;

import com.anhvan.vmr.util.RowMapperUtil;
import io.vertx.sqlclient.Row;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class History {
  public enum Type {
    TRANSFER,
    RECEIVE
  }

  private long id;
  private long userId;
  private long sender;
  private long receiver;
  private long amount;
  private long timestamp;
  private long balance;
  private String message;
  private Type type;

  public static History fromRow(Row row) {
    History history = RowMapperUtil.mapRow(row, History.class);

    // Set type
    String typeString = row.getString("type_string");
    if (typeString.equals("TRANSFER")) {
      history.setType(History.Type.TRANSFER);
    } else if (typeString.equals("RECEIVE")) {
      history.setType(History.Type.RECEIVE);
    }

    return history;
  }
}
