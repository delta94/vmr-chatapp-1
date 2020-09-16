let initialState = {
  webSocket: {
    webSocket: null,
    send: null,
    close: null
  }
};

export default function appReducer(state = initialState, action) {
  let data = action.data;

  switch (action.type) {
    case 'WS_CONNECTED':
      state = handleWsConnected(state, data);
      break;
    case 'LOGOUT':
      state.webSocket.close();
      state = initialState;
      break;
    default:
    // DO nothing
  }

  return state;
}

function handleWsConnected(state, data) {
  return Object.assign({}, state, {
    webSocket: {
      webSocket: data.webSocket,
      send: data.send,
      close: data.close
    }
  });
}

