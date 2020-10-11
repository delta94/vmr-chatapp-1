import {getGrpcTokenMetadata, getJwtToken} from "../util/auth-util";

const {
  UserListRequest,
  ClearUnreadMessageRequest,
  SetFriendStatusRequest
} = require('../proto/vmr/friend_pb');
const {Empty} = require('../proto/vmr/common_pb');
const {FriendServiceClient} = require('../proto/vmr/friend_grpc_web_pb');

const ENVOY_ROOT = process.env.REACT_APP_ENVOY_ROOT;

let friendServiceClient = new FriendServiceClient(ENVOY_ROOT, null, null);

export function queryUser(queryString) {
  return new Promise(resolve => {
    // Create request object
    let userListRq = new UserListRequest();
    userListRq.setQueryString(queryString);

    // Call grpc service
    friendServiceClient.queryUser(userListRq, getGrpcTokenMetadata(), (err, res) => {
      if (err) {
        console.log(err);
      } else {
        resolve(res.getUserList());
      }
    });
  });
}

export function getFriendList() {
  return new Promise((resolve, reject) => {
    let emptyRequest = new Empty();
    friendServiceClient.getFriendList(emptyRequest, {'x-jwt-token': getJwtToken()}, (err, res) => {
      if (err) {
        console.log('Connection error');
        reject(err);
      } else {
        let error = res.getError();
        if (error) {
          console.log(error);
          reject(error);
        } else {
          resolve(res);
        }
      }
    });
  });
}

export function getChatFriendList() {
  return new Promise((resolve, reject) => {
    let emptyRequest = new Empty();
    friendServiceClient.getChatFriendList(emptyRequest, getGrpcTokenMetadata(), (err, res) => {
      if (err) {
        console.log('Connection error');
        reject(err);
      } else {
        let error = res.getError();
        if (error) {
          console.log(error);
          reject(error);
        } else {
          resolve(res);
        }
      }
    });
  });
}

export function clearUnreadMessage(friendId) {
  let request = new ClearUnreadMessageRequest();
  request.setFriendId(friendId);

  friendServiceClient.clearUnreadMessage(request, getGrpcTokenMetadata(), (err, res) => {
    if (err) {
      console.log('Connection error');
    } else {
      let error = res.getError();
      if (error) {
        console.log(error);
      }
    }
  });
}

export function setFriendStatus(friendId, action) {
  return new Promise((resolve, reject) => {
    let request = new SetFriendStatusRequest();
    request.setFriendId(friendId);

    if (action === 'REMOVE') {
      request.setType(SetFriendStatusRequest.Type.REMOVE_FRIEND);
    } else if (action === 'ADD') {
      request.setType(SetFriendStatusRequest.Type.ADD_FRIEND);
    } else if (action === 'ACCEPT') {
      request.setType(SetFriendStatusRequest.Type.ACCEPT_FRIEND);
    } else if (action === 'REJECT') {
      request.setType(SetFriendStatusRequest.Type.REJECT_FRIEND);
    }

    friendServiceClient.setFriendStatus(request, getGrpcTokenMetadata(), (err, res) => {
      if (err) {
        console.log('Connection error');
        reject(err);
      } else {
        let error = res.getError();
        if (error) {
          console.log(error);
          reject(error);
        } else {
          resolve(res);
        }
      }
    });
  });
}
