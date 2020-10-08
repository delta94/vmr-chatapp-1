import {getUserId} from "../util/auth-util";

let initState = {
  friends: {},
  currentFriendId: null,
}

export default function friendReducer(state = initState, action) {
  let data = action.data;

  switch (action.type) {
    case 'UPDATE_FRIEND_LIST':
      state = updateUserList(state, data);
      break;
    case 'ONOFF':
      state = handleOnOffLine(state, data);
      break;
    case 'SET_CURRENT_CONVERSATION_ID':
      state = setCurrentConservationId(state, data);
      break;
    case 'LOGOUT':
      state = initState;
      break;
    case 'CHAT_RECEIVE':
      state = handleChatReceive(state, data);
      break;
    case 'CHAT_SENDBACK':
      state = handleChatSendback(state, data);
      break;
    default:

  }

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
  friends[data.senderId] = {...friend, lastMsg: data.message, lastMsgSender: data.senderId};

  return {
    ...state,
    friends: friends
  }
}

function updateUserList(state, userList) {
  let friends = {};
  for (let user of userList) {
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

function setCurrentConservationId(state, id) {
  return Object.assign({}, state, {
    currentFriendId: id
  });
}
