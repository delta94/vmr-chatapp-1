package com.anhvan.vmr.database;

import io.vertx.junit5.VertxExtension;
import io.vertx.mysqlclient.MySQLPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(VertxExtension.class)
public class FriendDatabaseServiceImplTest {
  DatabaseService dbService;
  TransactionManager transactionManager;
  MySQLPool mySQLPool;
  FriendDatabaseServiceImpl friendDbService;

  @BeforeEach
  void setUp() {
    dbService = Mockito.mock(DatabaseService.class);
    transactionManager = Mockito.mock(TransactionManager.class);
    mySQLPool = Mockito.mock(MySQLPool.class);
    Mockito.when(dbService.getPool()).thenReturn(mySQLPool);
    Mockito.when(dbService.getTransactionManager()).thenReturn(transactionManager);
    friendDbService = new FriendDatabaseServiceImpl(dbService);
  }
}
