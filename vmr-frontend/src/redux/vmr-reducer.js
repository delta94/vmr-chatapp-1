let initialState = {
  user: {
    jwt: localStorage.getItem("jwtToken"),
    userId: localStorage.getItem("userId")
  },
  userList: [],
  currentConversationId: null
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

function updateUserList(state, userList) {
  console.log(userList);
  return Object.assign({}, state, {
    userList
  });
}

function setCurrentConservationId(state, id) {
  return Object.assign({}, state, {
    currentConversationId: id
  });
}