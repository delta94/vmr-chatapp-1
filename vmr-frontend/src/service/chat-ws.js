import store from '../redux/vmr-store';
import {webSocketConnected, receiveMessage, sendbackMessage, onOffline, newUser} from "../redux/vmr-action";

const WEB_SOCKET_ROOT = process.env.REACT_APP_WS_ROOT;

function createMessage(type, data) {
  return {
    type, data
  };
}

let webSocketManager = {
  currentConn: null,
  retry: false,
  setNew(conn) {
    this.retry = true;
    if (this.currentConn) {
      this.currentConn.close();
    }
    this.currentConn = conn;
  },
  clean() {
    this.retry = false;
    if (this.currentConn) {
      this.currentConn.close();
      this.currentConn = null;
    }
  },
  isActive() {
    return Boolean(this.currentConn);
  }
};

export function wsConnect() {
  let token = localStorage.getItem("jwtToken");
  internalConnect(token);
}

function internalConnect(token) {
  // Get senderId
  let senderId = Number(localStorage.getItem("userId"));

  // Create new websocket connection
  let webSocket = new WebSocket(WEB_SOCKET_ROOT + `?token=${token}`);

  // When connect successful
  webSocket.onopen = () => {
    webSocketManager.setNew(webSocket);

    // Function to send chat message
    let send = function (receiverId, message) {
      let msg = createMessage('CHAT', {
        receiverId, message
      });
      webSocket.send(JSON.stringify(msg));
    };

    // Notify to redux
    store.dispatch(webSocketConnected(webSocketManager, send, () => {
      webSocketManager.clean()
    }));
  };

  // Handle chat message
  webSocket.onmessage = messageEvent => {
    let jsonMessage = JSON.parse(messageEvent.data);
    let {type, data} = jsonMessage;

    if (type === 'CHAT') {
      // Handle chat
      store.dispatch(receiveMessage(data));
    } else if (type === 'SEND_BACK') {
      // Handle sendback
      if (data.receiverId === senderId) {
        return;
      }
      store.dispatch(sendbackMessage(data));
    } else if (type === 'ONLINE') {
      store.dispatch(onOffline(data, true));
    } else if (type === 'OFFLINE') {
      store.dispatch(onOffline(data, false));
    } else if (type === 'NEW_USER') {
      store.dispatch(newUser(data));
    }
  };

  // Try to reconnect
  webSocket.onclose = () => {
    webSocketManager.clean();
    setTimeout(() => {
      if (!webSocketManager.isActive() && webSocketManager.retry) {
        internalConnect(token);
      }
    }, 1000);
  };

  webSocket.onerror = () => {
    webSocket.close();
  }
}

require('./grpc-service');