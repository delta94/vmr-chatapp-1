package com.anhvan.vmr.cache;

import com.anhvan.vmr.configs.CacheConfig;
import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.service.AsyncWorkerService;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.List;

@ExtendWith(VertxExtension.class)
@SuppressWarnings("unchecked")
public class ChatCacheServiceImplTest {
  ChatCacheServiceImpl chatCacheService;
  RedissonClient redissonClient;

  @BeforeEach
  void setUp() {
    redissonClient = Mockito.mock(RedissonClient.class);
    RedisCache redisCache = Mockito.mock(RedisCache.class);
    Mockito.when(redisCache.getRedissonClient()).thenReturn(redissonClient);

    AsyncWorkerService workerUtil = Mockito.mock(AsyncWorkerService.class);
    Mockito.doAnswer(
            invocationOnMock -> {
              Runnable job = invocationOnMock.getArgument(0);
              job.run();
              return null;
            })
        .when(workerUtil)
        .execute(ArgumentMatchers.any());

    CacheConfig cacheConfig = Mockito.mock(CacheConfig.class);
    Mockito.when(cacheConfig.getTimeToLive()).thenReturn(20);
    Mockito.when(cacheConfig.getNumMessagesCached()).thenReturn(20);

    chatCacheService = new ChatCacheServiceImpl(redissonClient, workerUtil, cacheConfig);
  }

  @Test
  void testCacheMessage() {
    RList<Message> messageListMock = (RList<Message>) Mockito.mock(RList.class);
    Mockito.when(redissonClient.<Message>getList("vmr:chat:1:2")).thenReturn(messageListMock);

    Message message =
        Message.builder()
            .id(1)
            .senderId(2)
            .receiverId(1)
            .message("Hello world")
            .timestamp(100)
            .build();

    chatCacheService.cacheMessage(message);

    Mockito.verify(messageListMock).add(message);
  }

  @Test
  void testCacheMessageExceedLimit() {
    RList<Message> messageListMock = (RList<Message>) Mockito.mock(RList.class);
    Mockito.when(redissonClient.<Message>getList("vmr:chat:1:2")).thenReturn(messageListMock);
    Mockito.when(messageListMock.size()).thenReturn(21);

    Message message =
        Message.builder()
            .id(1)
            .senderId(2)
            .receiverId(1)
            .message("Hello world")
            .timestamp(100)
            .build();

    chatCacheService.cacheMessage(message);

    Mockito.verify(messageListMock).add(message);
    Mockito.verify(messageListMock).remove(0);
  }

  @Test
  void testCacheListMessage() {
    RList<Message> cachedMessageListMock = (RList<Message>) Mockito.mock(RList.class);
    Mockito.when(redissonClient.<Message>getList("vmr:chat:1:2")).thenReturn(cachedMessageListMock);
    Mockito.when(cachedMessageListMock.size()).thenReturn(21);
    List<Message> messageListMock = (List<Message>) Mockito.mock(List.class);

    chatCacheService.cacheListMessage(messageListMock, 1, 2);

    Mockito.verify(cachedMessageListMock).clear();
    Mockito.verify(cachedMessageListMock).addAll(messageListMock);
  }

  @Test
  void testGetCachedMessage(VertxTestContext testContext) {
    RList<Message> cachedMessageListMock = (RList<Message>) Mockito.mock(RList.class);
    Mockito.when(cachedMessageListMock.size()).thenReturn(21);
    List<Message> messageListMock = (List<Message>) Mockito.mock(List.class);
    Mockito.when(cachedMessageListMock.readAll()).thenReturn(messageListMock);
    Mockito.when(cachedMessageListMock.isExists()).thenReturn(true);
    Mockito.when(redissonClient.<Message>getList("vmr:chat:1:2")).thenReturn(cachedMessageListMock);

    chatCacheService
        .getCacheMessages(1, 2)
        .onSuccess(
            messageList -> {
              Assertions.assertEquals(messageListMock, messageList);
              testContext.completeNow();
            })
        .onFailure(testContext::failNow);
  }
}
