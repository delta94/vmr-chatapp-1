package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.cache.TokenCacheService;
import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.ChatDBService;
import com.anhvan.vmr.database.UserDBService;
import com.anhvan.vmr.util.JwtUtil;
import com.anhvan.vmr.websocket.WebSocketService;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import io.vertx.core.Vertx;
import lombok.extern.log4j.Log4j2;

@Module
@Log4j2
public class ControllerModule {
  @Provides
  @IntoMap
  @StringKey("/")
  public Controller provideIndexController() {
    return new IndexController();
  }

  @Provides
  @IntoMap
  @StringKey("/api/public/login")
  public Controller provideLoginController(
      UserDBService userDBService, UserCacheService userCacheService, JwtUtil jwtUtil) {
    return LoginController.builder()
        .userDBService(userDBService)
        .userCacheService(userCacheService)
        .jwtUtil(jwtUtil)
        .build();
  }

  @Provides
  @IntoMap
  @StringKey("/api/public/register")
  public Controller provideRegisterController(
      UserDBService userDBService, JwtUtil jwtUtil, UserCacheService userCacheService) {
    return RegisterController.builder()
        .userCacheService(userCacheService)
        .jwtUtil(jwtUtil)
        .userDBService(userDBService)
        .build();
  }

  @Provides
  @IntoMap
  @StringKey("/api/protected/users")
  public Controller provideUserController(
      UserDBService userDBService,
      UserCacheService userCacheService,
      WebSocketService webSocketService) {
    return UserController.builder()
        .userDBService(userDBService)
        .userCacheService(userCacheService)
        .webSocketService(webSocketService)
        .build();
  }

  @Provides
  @IntoMap
  @StringKey("/api/protected/info")
  public Controller provideUserInfoController(
      Vertx vertx, UserDBService userDBService, UserCacheService userCacheService) {
    return UserInfoController.builder()
        .vertx(vertx)
        .userDBService(userDBService)
        .userCacheService(userCacheService)
        .build();
  }

  @Provides
  @IntoMap
  @StringKey("/api/protected/logout")
  public Controller provideLogoutController(TokenCacheService tokenCacheService) {
    return LogoutController.builder().tokenCacheService(tokenCacheService).build();
  }

  @Provides
  @IntoMap
  @StringKey("/api/protected/sockettoken")
  public Controller provideSocketTokenController(Vertx vertx, JwtUtil jwtUtil) {
    return SocketTokenController.builder().vertx(vertx).jwtUtil(jwtUtil).build();
  }

  @Provides
  @IntoMap
  @StringKey("/api/protected/chat")
  public Controller provideMessageListController(
      Vertx vertx, ChatDBService chatDBService, ChatCacheService chatCacheService) {
    log.trace("Provide chat api");
    return new MessageListController(chatCacheService, chatDBService, vertx);
  }
}
