package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.cache.TokenCacheService;
import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.ChatDatabaseService;
import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.service.JwtService;
import com.anhvan.vmr.websocket.WebSocketService;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import lombok.extern.log4j.Log4j2;

import javax.inject.Singleton;

@Module
@Log4j2
public class ControllerModule {
  @Provides
  @IntoMap
  @StringKey("/")
  public Controller provideIndexController() {
    log.info("Register index controller");
    return new IndexController();
  }

  @Provides
  @IntoMap
  @StringKey("/api/public/login")
  public Controller provideLoginController(
      UserDatabaseService userDBService, UserCacheService userCacheService, JwtService jwtService) {
    log.info("Register login controller");
    return LoginController.builder()
        .userDBService(userDBService)
        .userCacheService(userCacheService)
        .jwtService(jwtService)
        .build();
  }

  @Provides
  @IntoMap
  @StringKey("/api/public/register")
  public Controller provideRegisterController(
      UserDatabaseService userDBService,
      JwtService jwtService,
      UserCacheService userCacheService,
      WebSocketService webSocketService) {
    log.info("Register registration controller");
    return RegisterController.builder()
        .userCacheService(userCacheService)
        .jwtService(jwtService)
        .userDBService(userDBService)
        .webSocketService(webSocketService)
        .build();
  }

  @Provides
  @IntoMap
  @StringKey("/api/protected/logout")
  public Controller provideLogoutController(TokenCacheService tokenCacheService) {
    log.info("Register logout controller");
    return LogoutController.builder().tokenCacheService(tokenCacheService).build();
  }

  @Provides
  @IntoMap
  @StringKey("/api/protected/chat")
  public Controller provideMessageListController(
      ChatDatabaseService chatDatabaseService, ChatCacheService chatCacheService) {
    log.info("Register message list controller");
    return new MessageListController(chatCacheService, chatDatabaseService);
  }

  @Provides
  @Singleton
  public ControllerFactory ControllerFactory(ControllerFactoryImpl impl) {
    return impl;
  }
}
