import {getGrpcTokenMetadata, getJwtToken} from "../util/auth-util";

const {AddFriendRequest, AcceptFriendRequest, RejectFriendRequest} = require('../proto/vmr/friend_pb');
const {Empty} = require('../proto/vmr/empty_pb');
const {FriendServiceClient} = require('../proto/vmr/friend_grpc_web_pb');

const ENVOY_ROOT = process.env.REACT_APP_ENVOY_ROOT;

let friendServiceClient = new FriendServiceClient(ENVOY_ROOT, null, null);

export function addFriend(friendId) {
  return new Promise((resolve, reject) => {
    let addFriendRequest = new AddFriendRequest();
    addFriendRequest.setUserId(friendId);
    friendServiceClient.addFriend(addFriendRequest, {'x-jwt-token': getJwtToken()}, (err, res) => {
      if (err) {
        console.log('Connection error');
        reject(err);
      } else {
        let error = res.getError();
        if (error) {
          console.log(error);
          reject(error);
        } else {
          resolve('Add friend successfully');
        }
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
    friendServiceClient.getChatFriendList(emptyRequest, {'x-jwt-token': getJwtToken()}, (err, res) => {
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

export function acceptFriend(friendId) {
  return new Promise((resolve, reject) => {
    let acceptRequest = new AcceptFriendRequest();
    acceptRequest.setFriendId(friendId);
    friendServiceClient.acceptFriend(acceptRequest, getGrpcTokenMetadata(), (err, res) => {
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

export function rejectFriend(friendId) {
  return new Promise((resolve, reject) => {
    let rejectRequest = new RejectFriendRequest();
    rejectRequest.setFriendId(friendId);
    friendServiceClient.rejectFriend(rejectRequest, getGrpcTokenMetadata(), (err, res) => {
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
