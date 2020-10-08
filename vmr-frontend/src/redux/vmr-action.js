function createAction(type, data) {
  return {type, data};
}

export const actionType = {
  LOGIN_SUCCESS: 'LOGIN_SUCCESS',
  UPDATE_FRIEND_LIST: 'UPDATE_FRIEND_LIST',
  SET_CURRENT_FRIEND: 'SET_CURRENT_FRIEND',
  LOGOUT: 'LOGOUT',
  WS_CONNECTED: 'WS_CONNECTED',
  CHAT_RECEIVE: 'CHAT_RECEIVE',
  CHAT_SENDBACK: 'CHAT_SENDBACK',
  GET_MSG_FROM_API: 'GET_MSG_FROM_API',
  ONOFF: 'ONOFF',
  SET_SIDE_BAR: 'SIDEBAR_SET',
  SET_SEARCH_MODAL: 'SET_SEARCH_MODAL',
  SET_TAB: 'SET_TAB',
  FRIEND_RELOAD: 'FRIEND_RELOAD',
  SET_WALLET_TAB: 'SET_WALLET_TAB'
};

export function login(jwt, userId) {
  return createAction(actionType.LOGIN_SUCCESS, {jwt, userId});
}

export function updateFriendList(userList) {
  return createAction(actionType.UPDATE_FRIEND_LIST, userList);
}

export function updateCurrentFriend(id) {
  return createAction(actionType.SET_CURRENT_FRIEND, id);
}

export function logout() {
  return createAction(actionType.LOGOUT);
}

export function webSocketConnected(webSocket, send, close) {
  return createAction(actionType.WS_CONNECTED, {webSocket, send, close});
}

export function receiveMessage(message) {
  return createAction(actionType.CHAT_RECEIVE, message);
}

export function sendbackMessage(message) {
  return createAction(actionType.CHAT_SENDBACK, message);
}

export function getMessageFromAPI(data, friendId) {
  return createAction(actionType.GET_MSG_FROM_API, {...data, friendId});
}

export function onOffline(userId, status) {
  return createAction(actionType.ONOFF, {userId, status});
}

export function setSideBarActive(active) {
  return createAction(actionType.SET_SIDE_BAR, active);
}

export function setSearchUserModalActive(active) {
  return createAction(actionType.SET_SEARCH_MODAL, active);
}

export function setTab(tab) {
  return createAction(actionType.SET_TAB, tab);
}

export function friendReload() {
  return createAction(actionType.FRIEND_RELOAD);
}

export function setWalletTab(tab) {
  return createAction(actionType.SET_WALLET_TAB, tab);
}
