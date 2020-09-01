let initialState = {
  user: {
    jwt: localStorage.getItem("jwtToken"),
    userId: localStorage.getItem("userId")
  },
  userList: [],
  userMapHolder: {
    userMap: new Map()
  },
  currentConversationId: null,
  webSocket: {
    webSocket: null,
    send: null
  },
  chatMessagesHolder: {
    chatMessages: new Map()
  },
  scrollFlag: true,
};

export default function appReducer(state = initialState, action) {
  let data = action.data;
  switch (action.type) {
    case 'LOGIN_SUCCESS':
      state = handleLogin(state, data);
      break;
    case 'UPDATE_USER_LIST':
      state = updateUserList(state, data);
      break;
    case 'SET_CURRENT_CONVERSATION_ID':
      state = setCurrentConservationId(state, data);
      break;
    case 'LOGOUT':
      state = handleLogout(state)
      break;
    case 'WS_CONNECTED':
      state = handleWsConnected(state, data);
      break;
    case 'CHAT_RECEIVE':
      state = handleChatReceive(state, data);
      break;
    case 'CHAT_SENDBACK':
      state = handleChatSendback(state, data);
      break;
    case 'GET_MSG_FROM_API':
      state = handleGetMsgFromAPI(state, data);
      break;
  }
  return state;
}

function handleLogin(state, data) {
  return Object.assign({}, state, {
    user: {
      jwt: data.jwt,
      userId: data.userId
    }
  });
}

function handleLogout(state, data) {
  return Object.assign({}, state, {
    user: {
      jwt: null,
      userId: null
    },
    userList: [],
    userMapHolder: {
      userMap: new Map()
    },
    currentConversationId: null,
    webSocket: {
      webSocket: null,
      send: null
    },
    chatMessagesHolder: {
      chatMessages: new Map()
    }
  });
}

function updateUserList(state, userList) {
  let userMap = new Map();
  let chatMessages = state.chatMessagesHolder.chatMessages;
  for (let user of userList) {
    userMap.set(user.id, user);
    if (!chatMessages.has(user.id)) {
      chatMessages.set(user.id, []);
    }
  }
  return Object.assign({}, state, {
    userList,
    userMapHolder: {
      userMap
    },
    chatMessagesHolder: {
      chatMessages
    }
  });
}

function setCurrentConservationId(state, id) {
  return Object.assign({}, state, {
    currentConversationId: id
  });
}

function handleWsConnected(state, data) {
  console.log('ws connect', data);
  let newState = Object.assign({}, state, {
    webSocket: {
      webSocket: data.webSocket,
      send: data.send
    }
  });
  console.log(newState);
  return newState;
}

function handleChatReceive(state, data) {
  console.log("Handle receive", data);
  let chatMessages = state.chatMessagesHolder.chatMessages;
  let listMsg = chatMessages.get(data.senderId);
  listMsg.push(data);
  return Object.assign({}, state, {
    chatMessagesHolder: {
      chatMessages
    },
    scrollFlag: !state.scrollFlag
  });
}

function handleChatSendback(state, data) {
  console.log('Handle sendback', data);
  let chatMessages = state.chatMessagesHolder.chatMessages;
  let listMsg = chatMessages.get(data.receiverId);
  listMsg.push(data);
  return Object.assign({}, state, {
    chatMessagesHolder: {
      chatMessages
    },
    scrollFlag: !state.scrollFlag
  });
}

function handleGetMsgFromAPI(state, data) {
  console.log('Handle chat mesg from db');
  let chatMessages = state.chatMessagesHolder.chatMessages;
  let listMsg = chatMessages.get(data.friendId);
  chatMessages.set(data.friendId, [...data.messages, ...listMsg]);
  return Object.assign({}, state, {
    chatMessagesHolder: {
      chatMessages
    }
  });
}