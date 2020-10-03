package com.anhvan.vmr.database;

import com.anhvan.vmr.util.PasswordUtil;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PreparedQuery;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@ExtendWith(VertxExtension.class)
@SuppressWarnings("unchecked")
public class WalletDatabaseServiceImplTest {
  private MySQLPool pool;
  private WalletDatabaseServiceImpl walletDatabaseService;
  private PasswordUtil passwordUtil;

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
  void testTransfer(VertxTestContext testContext) {
    testContext.completeNow();
  }
}
