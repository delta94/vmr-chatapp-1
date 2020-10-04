package com.anhvan.vmr.database;

import com.anhvan.vmr.util.PasswordUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;

@ExtendWith(VertxExtension.class)
@SuppressWarnings("unchecked")
public class WalletDatabaseServiceImplTest {
  MySQLPool pool;
  WalletDatabaseServiceImpl walletDatabaseService;
  PasswordUtil passwordUtil;

  @BeforeEach
  void setUp() {
    pool = Mockito.mock(MySQLPool.class);
    passwordUtil = Mockito.mock(PasswordUtil.class);
    walletDatabaseService = new WalletDatabaseServiceImpl(pool, passwordUtil);
  }

  @Test
  void testGetHistory(VertxTestContext testContext) {
    PreparedQuery<RowSet<Row>> preparedQuery = Mockito.mock(PreparedQuery.class);

    Mockito.when(pool.preparedQuery(WalletDatabaseServiceImpl.HISTORY_QUERY))
        .thenReturn(preparedQuery);

    Mockito.doAnswer(
            invocationOnMock -> {
              Tuple tuple = invocationOnMock.getArgument(0);
              Assertions.assertEquals(1L, tuple.getLong(0));
              testContext.completeNow();
              return null;
            })
        .when(preparedQuery)
        .execute(ArgumentMatchers.any(), ArgumentMatchers.any());

    walletDatabaseService.getHistory(1);
  }

  @Test
  void testTransferPasswordValid(VertxTestContext testContext) {
    Mockito.when(passwordUtil.checkPassword(1, "1234578")).thenReturn(Future.succeededFuture(true));

    walletDatabaseService
        .checkPassword(TransferStateHolder.builder().senderId(1).password("1234578").build())
        .onComplete(
            ar -> {
              Assertions.assertTrue(ar.succeeded());
              testContext.completeNow();
            });
  }

  @Test
  void testCheckPasswordInvalid(VertxTestContext testContext) {
    Mockito.when(passwordUtil.checkPassword(1, "1234578")).thenReturn(Future.succeededFuture(true));
    Mockito.when(passwordUtil.checkPassword(1, "123457"))
        .thenReturn(Future.failedFuture("Password is invalid"));
    walletDatabaseService
        .checkPassword(TransferStateHolder.builder().senderId(1).password("123457").build())
        .onComplete(
            ar -> {
              Assertions.assertTrue(ar.failed());
              testContext.completeNow();
            });
  }

  @Test
  void testStartTransaction(VertxTestContext testContext) {
    SqlConnection sqlConnection = Mockito.mock(SqlConnection.class);
    AsyncResult<SqlConnection> connAr = Mockito.mock(AsyncResult.class);

    Mockito.when(connAr.succeeded()).thenReturn(true);
    Mockito.when(connAr.result()).thenReturn(sqlConnection);

    Mockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<SqlConnection>> handler = invocationOnMock.getArgument(0);
              handler.handle(connAr);
              Mockito.verify(sqlConnection).begin();
              testContext.completeNow();
              return null;
            })
        .when(pool)
        .getConnection(ArgumentMatchers.any());

    walletDatabaseService.startTransaction(TransferStateHolder.builder().build());
  }

  @Test
  void testCheckReceiverExist(VertxTestContext testContext) {
    SqlConnection sqlConnection = Mockito.mock(SqlConnection.class);
    PreparedQuery<RowSet<Row>> preparedQuery = Mockito.mock(PreparedQuery.class);
    RowSet<Row> rowSetMock = Mockito.mock(RowSet.class);
    AsyncResult<RowSet<Row>> asyncResult = Mockito.mock(AsyncResult.class);
    Mockito.when(sqlConnection.preparedQuery(WalletDatabaseServiceImpl.CHECK_USER_EXIST_QUERY))
        .thenReturn(preparedQuery);
    Mockito.when(asyncResult.succeeded()).thenReturn(true);
    Mockito.when(asyncResult.result()).thenReturn(rowSetMock);

    Mockito.doAnswer(
            invocationOnMock -> {
              Tuple t = invocationOnMock.getArgument(0);
              Assertions.assertEquals(2L, t.getLong(0));
              testContext.completeNow();
              return null;
            })
        .when(preparedQuery)
        .execute(ArgumentMatchers.any(), ArgumentMatchers.any());

    TransferStateHolder holder =
        TransferStateHolder.builder().conn(sqlConnection).receiverId(2).build();

    walletDatabaseService.checkReceiverExist(holder).onComplete(ar -> testContext.completeNow());
  }

  @Test
  void testCheckBalanceEnought(VertxTestContext testContext) {
    SqlConnection sqlConnection = Mockito.mock(SqlConnection.class);
    PreparedQuery<RowSet<Row>> preparedQuery = Mockito.mock(PreparedQuery.class);
    Mockito.when(sqlConnection.preparedQuery(WalletDatabaseServiceImpl.BALANCE_QUERY))
        .thenReturn(preparedQuery);

    Mockito.doAnswer(
            invocationOnMock -> {
              List<Tuple> tupleList = invocationOnMock.getArgument(0);
              Assertions.assertEquals(2, tupleList.size());
              testContext.completeNow();
              return null;
            })
        .when(preparedQuery)
        .executeBatch(ArgumentMatchers.any(), ArgumentMatchers.any());

    TransferStateHolder holder =
        TransferStateHolder.builder().conn(sqlConnection).receiverId(2).senderId(1).build();

    walletDatabaseService.checkBalanceEnough(holder).onComplete(ar -> testContext.completeNow());
  }
}
