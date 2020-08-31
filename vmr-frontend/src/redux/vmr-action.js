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

export function updateConservationId(id) {
  return {
    type: 'SET_CURRENT_CONVERSATION_ID',
    data: id
  }
}