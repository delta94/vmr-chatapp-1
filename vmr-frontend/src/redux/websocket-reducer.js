import {actionType} from "./vmr-action";

function createInitialState() {
  return {
    webSocketManager: null,
    send: null,
    close: null
  };
}

let initialState = createInitialState();

export default function appReducer(state = initialState, action) {
  let data = action.data;

  switch (action.type) {
    case actionType.WS_CONNECTED:
      state = handleWsConnected(state, data);
      break;
    case actionType.LOGOUT:
      if (state.close) {
        state.close();
      }
      state = createInitialState();
      break;
    default:
    // DO nothing
  }

  return state;
}

function handleWsConnected(state, data) {
  return {
    webSocketManager: data.webSocketManager,
    send: data.send,
    close: data.close
  };
}
