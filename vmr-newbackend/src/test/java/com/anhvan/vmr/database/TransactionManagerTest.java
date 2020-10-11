package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.FutureStateHolder;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@ExtendWith(VertxExtension.class)
@SuppressWarnings("unchecked")
public class TransactionManagerTest {
  FutureStateHolder stateHolder;
  MySQLPool pool;
  Transaction transaction;
  SqlConnection sqlConnection;
  TransactionManager transactionManager;

  @BeforeEach
  void setUp() {
    pool = Mockito.mock(MySQLPool.class);
    transaction = Mockito.mock(Transaction.class);
    stateHolder = new FutureStateHolder();
    sqlConnection = Mockito.mock(SqlConnection.class);
    transactionManager = new TransactionManager(pool, stateHolder);
  }

  @Test
  void testGetKey() {
    transactionManager.set("msg", "Hello world");
    Assertions.assertEquals("Hello world", transactionManager.get("msg"));
  }

  @Test
  void testBeginTransactionFailed(VertxTestContext vertxTestContext) {
    AsyncResult<SqlConnection> asyncResult = Mockito.mock(AsyncResult.class);
    Mockito.when(asyncResult.failed()).thenReturn(true);
    Mockito.when(asyncResult.cause()).thenReturn(new RuntimeException("Sample"));
    Mockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<SqlConnection>> handler = invocationOnMock.getArgument(0);
              handler.handle(asyncResult);
              return null;
            })
        .when(pool)
        .getConnection(ArgumentMatchers.any());

    transactionManager
        .begin()
        .onComplete(
            ar -> {
              Assertions.assertTrue(ar.failed());
              Assertions.assertEquals("Sample", ar.cause().getMessage());
              vertxTestContext.completeNow();
            });
  }

  @Test
  void testBeginTransactionSucceeded(VertxTestContext vertxTestContext) {
    AsyncResult<SqlConnection> asyncResult = Mockito.mock(AsyncResult.class);
    Mockito.when(asyncResult.failed()).thenReturn(false);
    Mockito.when(asyncResult.result()).thenReturn(sqlConnection);
    Mockito.when(sqlConnection.begin()).thenReturn(transaction);
    Mockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<SqlConnection>> handler = invocationOnMock.getArgument(0);
              handler.handle(asyncResult);
              return null;
            })
        .when(pool)
        .getConnection(ArgumentMatchers.any());

    transactionManager
        .begin()
        .onComplete(
            ar -> {
              Assertions.assertEquals(transaction, ar.result().getTransaction());
              ar.result().close();
              Mockito.verify(sqlConnection).close();
              vertxTestContext.completeNow();
            });
  }

  @Test
  void testCommitTransaction(VertxTestContext vertxTestContext) {
    AsyncResult<SqlConnection> asyncResult = Mockito.mock(AsyncResult.class);
    Mockito.when(asyncResult.failed()).thenReturn(false);
    Mockito.when(asyncResult.result()).thenReturn(sqlConnection);
    Mockito.when(sqlConnection.begin()).thenReturn(transaction);

    AsyncResult<Void> commitAsyncResult = Mockito.mock(AsyncResult.class);
    Mockito.when(asyncResult.failed()).thenReturn(false);

    Mockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<Void>> handler = invocationOnMock.getArgument(0);
              handler.handle(commitAsyncResult);
              return null;
            })
        .when(transaction)
        .commit(ArgumentMatchers.any());

    Mockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<SqlConnection>> handler = invocationOnMock.getArgument(0);
              handler.handle(asyncResult);
              return null;
            })
        .when(pool)
        .getConnection(ArgumentMatchers.any());

    transactionManager
        .begin()
        .onComplete(
            ar ->
                transactionManager
                    .commit()
                    .onComplete(
                        ar2 -> {
                          Assertions.assertTrue(ar2.succeeded());
                          vertxTestContext.completeNow();
                        }));
  }
}
