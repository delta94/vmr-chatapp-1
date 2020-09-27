package com.anhvan.vmr.util;

import com.anhvan.vmr.proto.Friend;

public class GrpcUtil {
  public static Friend.FriendStatus string2FriendStatus(String status) {
    if (status == null) {
      return Friend.FriendStatus.NOTHING;
    }
    if (status.equals("WAITING")) {
      return Friend.FriendStatus.WAITING;
    }
    if (status.equals("NOT_ANSWER")) {
      return Friend.FriendStatus.NOT_ANSWER;
    }
    return Friend.FriendStatus.FRIEND;
  }
}
