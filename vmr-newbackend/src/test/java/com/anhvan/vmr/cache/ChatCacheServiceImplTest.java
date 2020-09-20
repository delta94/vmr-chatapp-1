package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.CacheConfig;
import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.util.AsyncWorkerUtil;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

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

    AsyncWorkerUtil workerUtil = Mockito.mock(AsyncWorkerUtil.class);
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

    chatCacheService = new ChatCacheServiceImpl(redisCache, workerUtil, cacheConfig);
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
}
