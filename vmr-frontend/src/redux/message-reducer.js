let initState = {
  messages: {},
  scrollFlag: false
};

export default function messageReducer(state = initState, action) {
  let data = action.data;

  switch (action.type) {
    case 'UPDATE_FRIEND_LIST':
      state = initMessages(state, data);
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
    case 'LOGOUT':
      state = initState;
      break;
    default:
    // Do nothing
  }

  return state;
}

function initMessages(state, userList) {
  let messages = state.messages;
  for (let user of userList) {
    if (!messages[user.id]) {
      messages[user.id] = [];
    }
  }
  return Object.assign({}, state, {
    messages
  });
}

function handleChatReceive(state, data) {
  let {senderId} = data;

  let messages = state.messages;
  let msgList = messages[senderId];
  messages[senderId] = [...msgList, data];

  return Object.assign({}, state, {
    messages,
    scrollFlag: !state.scrollFlag
  });
}

function handleChatSendback(state, data) {
  let messages = state.messages;
  let msgList = messages[data.receiverId];
  messages[data.receiverId] = [...msgList, data];

  return Object.assign({}, state, {
    msgList,
    scrollFlag: !state.scrollFlag
  });
}

function handleGetMsgFromAPI(state, data) {
  let messages = state.messages;
  let msgList = messages[data.friendId];
  messages[data.friendId] = [...data.messages, ...msgList,];
  return Object.assign({}, state, {messages});
}
