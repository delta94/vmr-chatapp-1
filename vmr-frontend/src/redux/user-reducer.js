import {getJwtToken, getUserId} from "../util/auth-util";
import {actionType} from "./vmr-action";

function getInitState() {
  return {
    jwt: getJwtToken(),
    userId: getUserId()
  }
}

let initialUser = getInitState();

export default function userReducer(state = initialUser, action) {
  let data = action.data;

  switch (action.type) {
    case actionType.LOGIN_SUCCESS:
      state = handleLogin(state, data);
      break;
    case actionType.LOGOUT:
      console.log('Handle logout');
      state = getInitState();
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
