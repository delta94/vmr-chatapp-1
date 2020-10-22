# Grpc API Design

## 1. Truy vấn số dư, xem lịch sử, chuyển tiền

```protobuf
syntax = "proto3";

package vmr;

option java_package = "com.anhvan.vmr.proto";

import "vmr/common.proto";

service WalletService {
  rpc GetBalance(Empty) returns (BalanceResponse);
  rpc Transfer(TransferRequest) returns (TransferResponse);
  rpc GetHistoryWithOffset(GetHistoryWithOffsetRequest) returns (HistoryResponse);
  rpc RemindTransfer(TransferReminderRequest) returns (TransferReminderResponse);
  rpc GetTransferReminder(Empty) returns (TransferReminderListResponse);
}

// Get balance
message BalanceResponse {
  message Data {
    int64 balance = 1;
    int64 last_updated = 2;
    string user_name = 3;
    string name = 4;
  }

  Error error = 1;
  Data data = 2;
}

// History

message GetHistoryWithOffsetRequest {
  int64 offset = 1;
}

// Transfer
message TransferRequest {
  int64 request_id = 1;
  int64 receiver = 2;
  uint64 amount = 3;
  string message = 4;
  string password = 5;
}

message TransferResponse {
  Error error = 1;
}

message HistoryResponse {
  enum Type {
    TRANSFER = 0;
    RECEIVE = 1;
  }

  message Item {
    int64 id = 1;
    int64 sender = 2;
    int64 receiver = 3;
    int64 amount = 4;
    int64 timestamp = 5;
    string message = 6;
    int64 balance = 7;
    Type type = 8;
  }

  message Data {
    repeated Item item = 1;
  }

  Error error = 1;
  Data data = 2;
}

message TransferReminderRequest {
  int64 request_id = 1;
  int64 user_id = 2;
  int64 amount = 3;
  string content = 4;
}

message TransferReminderResponse {
  Error error = 1;
}

message TransferReminder {
  int64 id = 1;
  int64 timestamp = 2;
  int64 from = 3;
  int64 to = 4;
  int64 amount = 5;
  string content = 6;
}

message TransferReminderListResponse {
  Error error = 1;
  repeated TransferReminder reminder = 2;
}
```

## 2. Tìm kiếm và kết bạn

```protobuf
syntax = "proto3";

package vmr;

option java_package = "com.anhvan.vmr.proto";

import "vmr/common.proto";

// Friend services
service FriendService {
  rpc GetFriendList(Empty) returns (FriendListResponse);
  rpc GetChatFriendList(Empty) returns (FriendListResponse);
  rpc QueryUser(UserListRequest) returns (UserListResponse);
  rpc ClearUnreadMessage(ClearUnreadMessageRequest) returns (ClearUnreadMessageResponse);
  rpc SetFriendStatus(SetFriendStatusRequest) returns (SetFriendStatusResponse);
  rpc GetUserInfo(GetUserInfoRequest) returns (GetUserInfoResponse);
}

// Get user info
message GetUserInfoRequest {
  int64 user_id = 1;
}

message GetUserInfoResponse {
  Error error = 1;
  UserResponse user = 2;
}

// Get friend list
message FriendListResponse {
  Error error = 1;
  repeated FriendInfo friend_info = 2;
}

message FriendInfo {
  int64 id = 1;
  string username = 2;
  string name = 3;
  bool online = 4;
  string last_message = 5;
  string last_message_type = 6;
  int64 last_message_sender = 7;
  int64 last_message_timestamp = 8;
  int64 num_unread_message = 9;
  FriendStatus friend_status = 10;
}

// Get user list (find friend)
message UserListRequest {
  string query_string = 1;
}

message UserListResponse {
  Error error = 1;
  repeated UserResponse user = 2;
}

message UserResponse {
  int64 id = 1;
  string username = 2;
  string name = 3;
  FriendStatus friend_status = 4;
}

enum FriendStatus {
  FRIEND = 0;
  WAITING = 1;
  NOT_ANSWER = 2;
  NOTHING = 3;
  REMOVED = 4;
}

// Clear unread message
message ClearUnreadMessageRequest {
  int64  friend_id = 1;
}

message ClearUnreadMessageResponse {
  Error error = 1;
}

// Set friend status
message SetFriendStatusRequest {
  enum Type {
    ADD_FRIEND = 0;
    ACCEPT_FRIEND = 1;
    REJECT_FRIEND = 2;
    REMOVE_FRIEND = 3;
  }

  Type type = 1;
  int64 friend_id = 2;
}

message SetFriendStatusResponse {
  Error error = 1;
}
```

## 3. Các message hỗ trợ

```protobuf
syntax = "proto3";

package vmr;

option java_package = "com.anhvan.vmr.proto";

enum ErrorCode {
  PASSWORD_INVALID = 0;
  BALANCE_NOT_ENOUGH = 1;
  RECEIVER_NOT_EXIST = 2;
  INTERNAL_SERVER_ERROR = 3;
  REQUEST_EXISTED = 4;
  FAILUE = 5;
  AMOUNT_NOT_VALID = 6;
}

message Error {
  ErrorCode code = 1;
  string message = 2;
  map<string, string> extra = 3;
}

message Empty {}
```
