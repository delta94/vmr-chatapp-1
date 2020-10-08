package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.database.ChatDatabaseService;
import com.anhvan.vmr.entity.BaseRequest;
import com.anhvan.vmr.entity.BaseResponse;
import com.anhvan.vmr.model.Message;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@AllArgsConstructor
@Log4j2
public class MessageListController extends BaseController {
  private ChatCacheService chatCacheService;
  private ChatDatabaseService chatDatabaseService;

  @Override
  @RoutePath("/:friendId/:offset")
  public Future<BaseResponse> handleGet(BaseRequest baseRequest) {
    Promise<BaseResponse> responsePromise = Promise.promise();

    HttpServerRequest request = baseRequest.getRequest();

    int userId = baseRequest.getPrincipal().getInteger("userId");
    int friendId = Integer.parseInt(request.getParam("friendId"));
    int offset = Integer.parseInt(request.getParam("offset"));

    //    if (offset == 0) {
    //      // First load
    //      Future<List<Message>> chatMessages = chatCacheService.getCacheMessage(userId, friendId);
    //
    //      // Cache hit
    //      chatMessages.onSuccess(
    //          wsMessages -> {
    //            log.debug("Get chat messages - cache hit userId1:{} - userId2:{}", userId,
    // friendId);
    //            JsonObject jsonResponse = new JsonObject();
    //            jsonResponse.put("messages", wsMessages);
    //            jsonResponse.put("newOffset", offset + wsMessages.size());
    //            responsePromise.complete(
    //                BaseResponse.builder()
    //                    .message("Get chat messages successfully")
    //                    .statusCode(HttpResponseStatus.OK.code())
    //                    .data(jsonResponse)
    //                    .build());
    //          });
    //
    //      // Cache miss
    //      chatMessages.onFailure(
    //          throwable -> {
    //            log.debug(
    //                "Fail to load chat messages from cache user1:{}-user2:{}",
    //                userId,
    //                friendId,
    //                throwable);
    //            getFromDB(userId, friendId, offset, responsePromise, true);
    //          });
    //    } else {
    //      // Load more
    //      getFromDB(userId, friendId, offset, responsePromise, false);
    //    }
    getFromDB(userId, friendId, offset, responsePromise, false);
    return responsePromise.future();
  }

  private void getFromDB(
      int userId,
      int friendId,
      int offset,
      Promise<BaseResponse> responsePromise,
      boolean isCached) {

    chatDatabaseService
        .getChatMessages(userId, friendId, offset)
        .onComplete(
            result -> {
              if (result.failed()) {
                log.error("Error when get chat messages", result.cause());
                responsePromise.fail(result.cause());
                return;
              }

              // Get the messages from databases
              List<Message> listMessage = result.result();

              JsonObject jsonResponse = new JsonObject();
              jsonResponse.put("messages", listMessage);
              jsonResponse.put("newOffset", offset + listMessage.size());

              responsePromise.complete(
                  BaseResponse.builder()
                      .statusCode(HttpResponseStatus.OK.code())
                      .data(jsonResponse)
                      .message("Get chat mesages successfully")
                      .build());

              // Cache
              if (isCached) {
                chatCacheService.cacheListMessage(listMessage, userId, friendId);
              }
            });
  }
}
