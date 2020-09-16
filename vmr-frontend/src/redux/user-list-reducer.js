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
    default:
    //Do nothing
  }

  return state;
}

function updateUserList(state, userList) {
  let userMap = new Map();
  console.log(userList);
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
    user.online = data.status
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
