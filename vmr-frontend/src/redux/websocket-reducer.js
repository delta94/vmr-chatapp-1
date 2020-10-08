import {actionType} from "./vmr-action";

let initialState = {
  webSocketManager: null,
  send: null,
  close: null
};

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
      state = initialState;
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

