package com.anhvan.vmr.database;

import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(VertxExtension.class)
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
  void testAddFriend(VertxTestContext testContext) {
    friendDbService
        .addFriend(0, 1)
        .onComplete(
            ar -> {
              testContext.completeNow();
            });
  }
}
