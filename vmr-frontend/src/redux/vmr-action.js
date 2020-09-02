export function login(jwt, userId) {
  return {
    type: 'LOGIN_SUCCESS',
    data: {
      jwt, userId
    }
  }
}

export function updateUserList(userList) {
  return {
    type: 'UPDATE_USER_LIST',
    data: userList
  }
}

export function updateActiveConservationId(id) {
  return {
    type: 'SET_CURRENT_CONVERSATION_ID',
    data: id
  }
}

export function logout() {
  return {
    type: 'LOGOUT'
  }
}

export function webSocketConnected(webSocket, send) {
  return {
    type: 'WS_CONNECTED',
    data: {
      webSocket,
      send
    }
  }
}

export function receiveMessage(message) {
  return {
    type: 'CHAT_RECEIVE',
    data: message
  }
}

export function sendbackMessage(message) {
  return {
    type: 'CHAT_SENDBACK',
    data: message
  }
}

export function getMessageFromAPI(data, friendId) {
  data.friendId = friendId;
  return {
    type: 'GET_MSG_FROM_API',
    data
  }
}

export function onOffline(userId, status) {
  return {
    type: 'ONOFF',
    data: {
      userId, status
    }
  }
}
