package com.anhvan.vmr.database;

import com.anhvan.vmr.model.Message;
import io.vertx.core.Handler;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PreparedQuery;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.time.Instant;

@ExtendWith(VertxExtension.class)
@Log4j2
@SuppressWarnings("unchecked")
public class ChatDatabaseServiceImplTest {
  static MySQLPool pool;
  static ChatDatabaseServiceImpl chatDBService;

  @BeforeAll
  static void setUp() {
    pool = Mockito.mock(MySQLPool.class);
    DatabaseService databaseService = Mockito.mock(DatabaseService.class);
    Mockito.when(databaseService.getPool()).thenReturn(pool);
    chatDBService = new ChatDatabaseServiceImpl(databaseService.getPool());
  }

  @Test
  void testAddChat(VertxTestContext vertxTestContext) {
    PreparedQuery<RowSet<Row>> preparedQuery =
        (PreparedQuery<RowSet<Row>>) Mockito.mock(PreparedQuery.class);

    Mockito.when(pool.preparedQuery(ChatDatabaseServiceImpl.INSERT_MESSAGE_STMT))
        .thenReturn(preparedQuery);

    Mockito.doAnswer(
            invocationOnMock -> {
              Tuple tuple = invocationOnMock.getArgument(0);
              Assertions.assertEquals(1, tuple.getInteger(0));
              Assertions.assertEquals(2, tuple.getInteger(1));
              Assertions.assertEquals("Hello", tuple.getString(2));
              vertxTestContext.completeNow();
              return null;
            })
        .when(preparedQuery)
        .execute(ArgumentMatchers.any(Tuple.class), ArgumentMatchers.any(Handler.class));

    chatDBService.addChat(
        Message.builder()
            .senderId(1)
            .receiverId(2)
            .message("Hello")
            .timestamp(Instant.now().getEpochSecond())
            .build());
  }

  @Test
  void testGetChat() {
    PreparedQuery<RowSet<Row>> preparedQuery =
        (PreparedQuery<RowSet<Row>>) Mockito.mock(PreparedQuery.class);

    Mockito.when(pool.preparedQuery(ChatDatabaseServiceImpl.GET_MESSAGE_STMT))
        .thenReturn(preparedQuery);

    chatDBService.getChatMessages(1, 2, 0);

    Mockito.verify(preparedQuery).execute(ArgumentMatchers.any(), ArgumentMatchers.any());
  }
}
