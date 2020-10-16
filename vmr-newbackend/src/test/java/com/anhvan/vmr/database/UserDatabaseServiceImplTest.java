package com.anhvan.vmr.database;

import com.anhvan.vmr.service.AsyncWorkerService;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@ExtendWith(VertxExtension.class)
@SuppressWarnings("unchecked")
@Log4j2
public class UserDatabaseServiceImplTest {
  static DatabaseService dbService = Mockito.mock(DatabaseService.class);
  static MySQLPool mySQLPool = Mockito.mock(MySQLPool.class);
  static AsyncWorkerService asyncWorkerService;
  static UserDatabaseServiceImpl userDatabaseService;

  @BeforeAll
  static void setUp(Vertx vertx) {
    Mockito.when(dbService.getPool()).thenReturn(mySQLPool);
    asyncWorkerService = new AsyncWorkerService(vertx);
    userDatabaseService = new UserDatabaseServiceImpl(dbService.getPool(), asyncWorkerService);
  }

  @Test
  void testGetUserByUsername(VertxTestContext testContext) {
    PreparedQuery<RowSet<Row>> preparedQuery =
        (PreparedQuery<RowSet<Row>>) Mockito.mock(PreparedQuery.class);

    Mockito.when(mySQLPool.preparedQuery(UserDatabaseServiceImpl.FIND_BY_USERNAME_STMT))
        .thenReturn(preparedQuery);

    Mockito.doAnswer(
            invocationOnMock -> {
              log.trace("Execute");
              Tuple tuple = invocationOnMock.getArgument(0);
              Assertions.assertEquals("anhvan", tuple.getString(0));
              testContext.completeNow();
              return null;
            })
        .when(preparedQuery)
        .execute(ArgumentMatchers.any(), ArgumentMatchers.any());

    userDatabaseService.getUserByUsername("anhvan");
  }

  @Test
  void testGetListUser(VertxTestContext testContext) {
    Query<RowSet<Row>> query = Mockito.mock(Query.class);

    Mockito.when(mySQLPool.query(UserDatabaseServiceImpl.FIND_ALL_USER_STMT)).thenReturn(query);

    Mockito.doAnswer(
            invocationOnMock -> {
              testContext.completeNow();
              return null;
            })
        .when(query)
        .execute(ArgumentMatchers.any());

    userDatabaseService
        .getListUser()
        .onComplete(
            ar -> {
              Mockito.verify(query).execute(ArgumentMatchers.any());
              testContext.completeNow();
            });
  }

  @Test
  void testGetUserById(VertxTestContext testContext) {
    PreparedQuery<RowSet<Row>> preparedQuery =
        (PreparedQuery<RowSet<Row>>) Mockito.mock(PreparedQuery.class);

    Mockito.when(mySQLPool.preparedQuery(UserDatabaseServiceImpl.FIND_BY_ID_STMT))
        .thenReturn(preparedQuery);

    Mockito.doAnswer(
            invocationOnMock -> {
              log.trace("Execute");
              Tuple tuple = invocationOnMock.getArgument(0);
              Assertions.assertEquals(1, tuple.getInteger(0));
              testContext.completeNow();
              return null;
            })
        .when(preparedQuery)
        .execute(ArgumentMatchers.any(), ArgumentMatchers.any());

    userDatabaseService.getUserById(1);
  }
}
