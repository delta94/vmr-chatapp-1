import {getUserId} from "../util/auth-util";
import {actionType} from "./vmr-action";

let initState = {
  friends: {},
  currentFriendId: null,
}

export default function friendReducer(state = initState, action) {
  let data = action.data;

  switch (action.type) {
    case actionType.UPDATE_FRIEND_LIST:
      state = updateUserList(state, data);
      break;
    case actionType.ONOFF:
      state = handleOnOffLine(state, data);
      break;
    case actionType.SET_CURRENT_FRIEND:
      state = setCurrentFriend(state, data);
      break;
    case actionType.LOGOUT:
      state = initState;
      break;
    case actionType.CHAT_RECEIVE:
      state = handleChatReceive(state, data);
      break;
    case actionType.CHAT_SENDBACK:
      state = handleChatSendback(state, data);
      break;
    case actionType.CLEAR_NOTI:
      state = handleClearNotifications(state, data);
      break;
    default:
    // do nothing
  }

  return state;
}

function handleClearNotifications(state, friendId) {
  let friend = state.friends[friendId];
  state.friends[friendId] = {...friend, numNotifications: 0};
  return state;
}

function handleChatSendback(state, data) {
  let friends = state.friends;
  let friend = friends[data.receiverId];
  friends[data.receiverId] = {...friend, lastMsg: data.message, lastMsgSender: getUserId()};

  return {
    ...state,
    friends
  }
}

function handleChatReceive(state, data) {
  let friends = state.friends;
  let friend = friends[data.senderId];
  friend.numNotifications += 1;
  friends[data.senderId] = {...friend, lastMsg: data.message, lastMsgSender: data.senderId};
  return {
    ...state,
    friends: friends
  }
}

function updateUserList(state, userList) {
  let friends = {};
  for (let user of userList) {
    user.numNotifications = 0;
    friends[user.id] = user;
  }
  return Object.assign({}, state, {
    friends
  });
}

function handleOnOffLine(state, data) {
  let friend = state.friends[data.userId];
  let newFriends = state.friends;
  if (friend) {
    newFriends[data.userId] = {...friend, online: data.status};
  }

  return Object.assign({}, state, {
    friends: newFriends
  });
}

function setCurrentFriend(state, id) {
  return {
    ...state, currentFriendId: id
  }
}
