import {getUserId} from "../util/auth-util";

let initState = {
  userList: [],
  userMapHolder: {
    userMap: new Map()
  },
  currentConversationId: null,
}

export default function userListReducer(state = initState, action) {
  let data = action.data;

  switch (action.type) {
    case 'UPDATE_USER_LIST':
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
    case 'NEW_USER':
      state = handleNewUser(state, data);
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
  let userMap = state.userMapHolder.userMap;

  let user = userMap.get(data.receiverId);

  user.lastMsg = data.message;
  user.lastMsgSender = getUserId();

  return {
    ...state,
    userMapHolder: {
      userMap
    }
  }
}

function handleChatReceive(state, data) {
  let userMap = state.userMapHolder.userMap;

  let user = userMap.get(data.senderId);
  user.lastMsg = data.message;
  user.lastMsgSender = data.senderId;

  return {
    ...state,
    userMapHolder: {
      userMap
    }
  }
}

function handleNewUser(state, user) {
  user.online = true;
  let userMap = state.userMapHolder.userMap;
  userMap.set(user.id, user);
  return Object.assign({}, state, {
    userList: [...state.userList, user],
    userMapHolder: {
      userMap
    }
  })
}

function updateUserList(state, userList) {
  let userMap = new Map();
  for (let user of userList) {
    userMap.set(user.id, user);
  }
  return Object.assign({}, state, {
    userList,
    userMapHolder: {
      userMap
    }
  });
}

function handleOnOffLine(state, data) {
  let userMap = state.userMapHolder.userMap;

  if (userMap === null) {
    return state;
  }

  let user = userMap.get(data.userId);
  if (user) {
    user.online = data.status;
    userMap.set(data.userId, user);
  }

  return Object.assign({}, state, {
    userMapHolder: {
      userMap
    }
  });
}

function setCurrentConservationId(state, id) {
  return Object.assign({}, state, {
    currentConversationId: id
  });
}
