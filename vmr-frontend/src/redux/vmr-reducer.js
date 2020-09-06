let initialState = {
  webSocket: {
    webSocket: null,
    send: null
  }
};

export default function appReducer(state = initialState, action) {
  let data = action.data;
  switch (action.type) {
    case 'WS_CONNECTED':
      state = handleWsConnected(state, data);
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
      send: data.send
    }
  });
}

