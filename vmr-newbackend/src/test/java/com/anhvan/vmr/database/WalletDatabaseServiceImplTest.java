package com.anhvan.vmr.database;

import com.anhvan.vmr.service.PasswordService;
import io.vertx.core.AsyncResult;
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
  PasswordService passwordService;

  @BeforeEach
  void setUp() {
    pool = Mockito.mock(MySQLPool.class);
    passwordService = Mockito.mock(PasswordService.class);
    walletDatabaseService = new WalletDatabaseServiceImpl(pool, passwordService, null);
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
