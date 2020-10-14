package com.anhvan.vmr.database;

import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PreparedQuery;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@ExtendWith(VertxExtension.class)
@SuppressWarnings("unchecked")
public class FriendDatabaseServiceImplTest {
  DatabaseService dbService;
  TransactionManager transactionManager;
  MySQLPool mySQLPool;
  FriendDatabaseServiceImpl friendDbService;
  Transaction transaction;

  @BeforeEach
  void setUp() {
    dbService = Mockito.mock(DatabaseService.class);
    transactionManager = Mockito.mock(TransactionManager.class);
    transaction = Mockito.mock(Transaction.class);
    mySQLPool = Mockito.mock(MySQLPool.class);

    Mockito.when(dbService.getPool()).thenReturn(mySQLPool);
    Mockito.when(dbService.getTransactionManager()).thenReturn(transactionManager);
    Mockito.when(transactionManager.getTransaction()).thenReturn(transaction);
    Mockito.when(transactionManager.begin()).thenReturn(Future.succeededFuture(transactionManager));

    friendDbService = new FriendDatabaseServiceImpl(dbService);
  }

  @Test
  void testGetFriendList(VertxTestContext testContext) {
    PreparedQuery<RowSet<Row>> preparedQuery = Mockito.mock(PreparedQuery.class);
    Mockito.when(mySQLPool.preparedQuery(FriendDatabaseServiceImpl.GET_FRIENDS_STMT))
        .thenReturn(preparedQuery);
    Mockito.doAnswer(
            invocationOnMock -> {
              testContext.completeNow();
              return null;
            })
        .when(preparedQuery)
        .execute(ArgumentMatchers.any(), ArgumentMatchers.any());
    friendDbService.getFriendList(1);
  }
}
