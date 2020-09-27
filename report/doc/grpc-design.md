# Grpc API Design

## 1. Chức năng xem số dư

```protobuf
syntax = "proto3";

package vmr;

option java_package = "com.anhvan.vmr.proto";

import "vmr/empty.proto";
import "vmr/error.proto";

service BalanceService {
  rpc GetBalance(Empty) returns (BalanceResponse){};
}

message BalanceResponse {
  Error error = 1;
  int64 balance = 2;
  int64 last_update_timestamp = 3;
}
```

## 2. Chức năng chuyển tiền

```protobuf
syntax = "proto3";

package vmr;

option java_package = "com.anhvan.vmr.proto";

import "vmr/error.proto";

service TransferService {
  rpc Transfer(TransferRequest) returns (TransferResponse);
}

message TransferRequest {
  int64 to = 1;
  int64 amount = 2;
  string transfer_message = 3;
  int64 request_id = 4;
}

message TransferResponse {
  Error error = 1;
  int64 new_balance = 3;
  int64 timestamp = 4;
}
```

## 3. Chức năng xem lịch sử

```protobuf
syntax = "proto3";

package vmr;

option java_package = "com.anhvan.vmr.proto";

import "vmr/empty.proto";
import "vmr/error.proto";

service HistoryService {
  rpc GetHistory(Empty) returns (HistoryListResponse);
}

enum HistoryResponseType {
  TRANSFER = 0;
  RECEIVE = 1;
}

message TransferHistoryResponse {
  int64 to = 1;
  int64 timestamp = 2;
  int64 amount = 3;
  string message = 4;
}

message ReceiveHistoryResponse {
  int64 from = 1;
  int64 timestamp = 2;
  int64 amount = 3;
  string message = 4;
}

message HistoryResponse {
  HistoryResponseType type = 1;
  TransferHistoryResponse transfer_response = 2;
  ReceiveHistoryResponse receive_response = 3;
}

message HistoryListResponse {
  Error error = 1;
  repeated HistoryResponse history_response = 2;
}
```

## 4. Chức năng nhắc chuyển tiền

```protobuf
syntax = "proto3";

package vmr;

option java_package = "com.anhvan.vmr.proto";

import "vmr/error.proto";

message TransferReminderRequest {
  int64 user_id = 1;
  int64 amount = 2;
  string message = 3;
  int64 request_id = 4;
}

message TransferReminderResponse {
  Error error = 1;
}

service TransferReminderService {
  rpc RemindTransfer(TransferReminderRequest) returns (TransferReminderResponse);
}
```

## 5. Hiện thông báo

Thông báo realtime vẫn sử dụng web socket.

```protobuf
syntax = "proto3";

package vmr;

option java_package = "com.anhvan.vmr.proto";

enum NotificationType {
  RECEIVE = 0;
  TRANSFER_REMINDER = 1;
}

message Notification {
  NotificationType noti_type = 1;
  ReceiveNotification receive_noti = 2;
  TransferReminderNotification remind_noti = 3;
}

message ReceiveNotification {
  int64 from = 1;
  int64 amount = 2;
  string message = 3;
  int64 timestamp = 4;
}

message TransferReminderNotification {
  int64 from = 1;
  int64 amount = 2;
  string message = 3;
  int64 timestamp = 4;
}
```

## 6. Tìm kiếm và kết bạn (bổ sung module trước)

```protobuf
syntax = "proto3";

package vmr;

option java_package = "com.anhvan.vmr.proto";

import "vmr/error.proto";
import "vmr/empty.proto";

service FriendService {
  rpc AddFriend(AddFriendRequest) returns (AddFriendResponse);
  rpc GetFriendList(Empty) returns (FriendListResponse);
  rpc AcceptFriend(AcceptFriendRequest) returns (AcceptFriendResponse);
  rpc RejectFriend(RejectFriendRequest) returns (RejectFriendResponse);
  rpc GetChatFriendList(Empty) returns (FriendListResponse);
}

message AddFriendRequest {
  int64 user_id = 1;
}

message AddFriendResponse {
  Error error = 1;
}

enum FriendStatus {
  FRIEND = 0;
  WAITING = 1;
  NOT_ANSWER = 2;
  NOTHING = 3;
}

message FriendInfo {
  int64 id = 1;
  string username = 2;
  string name = 3;
  bool online = 4;
  FriendStatus friend_status = 5;
}

message FriendListResponse {
  Error error = 1;
  repeated FriendInfo friendInfo = 2;
}

message AcceptFriendRequest {
  int64 friend_id = 1;
}

message AcceptFriendResponse {
  Error error = 1;
}

message RejectFriendRequest {
  int64 friend_id = 1;
}

message RejectFriendResponse {
  Error error = 1;
}
```

## 7. Error message

```protobuf
syntax = "proto3";

package vmr;

option java_package = "com.anhvan.vmr.proto";

enum ErrorCode {
  FAILUE = 0;
}

message Error {
  ErrorCode code = 1;
  string message = 2;
  map<string, string> extra = 3;
}
```
