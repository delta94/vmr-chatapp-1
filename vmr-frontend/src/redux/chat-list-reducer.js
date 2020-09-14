let initState = {
  chatMessagesHolder: {
    chatMessages: new Map()
  },
  scrollFlag: false
}

export default function chatListReducer(state = initState, action) {
  let data = action.data;

  switch (action.type) {
    case 'UPDATE_USER_LIST':
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
    default:
    // Do nothing
  }

  return state;
}

function initMessages(state, userList) {
  let chatMessages = state.chatMessagesHolder.chatMessages;
  for (let user of userList) {
    if (!chatMessages.has(user.id)) {
      chatMessages.set(user.id, []);
    }
  }
  return Object.assign({}, state, {
    chatMessagesHolder: {
      chatMessages
    }
  });
}

function handleChatReceive(state, data) {
  let chatMessages = state.chatMessagesHolder.chatMessages;
  let {senderId} = data;
  let listMsg = chatMessages.get(senderId);
  listMsg.push(data);
  return Object.assign({}, state, {
    chatMessagesHolder: {
      chatMessages
    },
    scrollFlag: !state.scrollFlag
  });
}

function handleChatSendback(state, data) {
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
  let chatMessages = state.chatMessagesHolder.chatMessages;
  let listMsg = chatMessages.get(data.friendId);
  chatMessages.set(data.friendId, [...data.messages, ...listMsg]);
  return Object.assign({}, state, {
    chatMessagesHolder: {
      chatMessages
    }
  });
}
