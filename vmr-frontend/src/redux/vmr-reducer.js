let initialState = {
  user: {
    jwt: localStorage.getItem("jwtToken"),
    userId: localStorage.getItem("userId")
  },
  userList: [],
  userMap: new Map(),
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
    case 'LOGOUT':
      state = handleLogout(state)
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
    }
  });
}

function updateUserList(state, userList) {
  console.log(userList);
  let userMap = new Map();
  for (let user of userList) {
    userMap.set(user.id, user);
  }
  return Object.assign({}, state, {
    userList,
    userMap
  });
}

function setCurrentConservationId(state, id) {
  return Object.assign({}, state, {
    currentConversationId: id
  });
}