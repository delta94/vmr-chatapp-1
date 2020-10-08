import {getJwtToken, getUserId} from "../util/auth-util";
import {actionType} from "./vmr-action";

let initialUser = {
  jwt: getJwtToken(),
  userId: getUserId()
};

export default function userReducer(state = initialUser, action) {
  let data = action.data;

  switch (action.type) {
    case actionType.LOGIN_SUCCESS:
      state = handleLogin(state, data);
      break;
    case 'LOGOUT':
      state = handleLogout(state)
      break;
    default:
    // Do notthing
  }

  return state;
}

function handleLogin(state, data) {
  return Object.assign({}, state, {
    jwt: data.jwt,
    userId: data.userId
  });
}

function handleLogout(state) {
  return Object.assign({}, state, {
    jwt: null,
    userId: null
  });
}
