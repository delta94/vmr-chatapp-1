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
public class HistoryItemResponse {
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

  public static HistoryItemResponse fromRow(Row row) {
    HistoryItemResponse history = RowMapperUtil.mapRow(row, HistoryItemResponse.class);

    // Set type
    String typeString = row.getString("type_string");
    history.setType(Type.valueOf(typeString));

    return history;
  }
}
