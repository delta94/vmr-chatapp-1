package com.anhvan.vmr.database;

import com.anhvan.vmr.service.PasswordService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.SqlConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

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
}
