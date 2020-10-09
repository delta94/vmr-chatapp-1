import store from '../redux/vmr-store';
import {webSocketConnected, receiveMessage, sendbackMessage, onOffline} from "../redux/vmr-action";
import {getJwtToken} from "../util/auth-util";

const WEB_SOCKET_ROOT = process.env.REACT_APP_WS_ROOT;

function createMessage(type, data) {
  return {
    type, data
  };
}

const messageType = {
  CHAT: 'CHAT',
  SEND_BACK: 'SEND_BACK',
  ONLINE: 'ONLINE',
  OFFLINE: "OFFLINE"
}

let webSocketManager = {
  currentConn: null,
  setNew(conn) {
    if (this.isActive()) {
      this.currentConn.close();
    }
    this.currentConn = conn;
  },
  clean() {
    if (this.isActive()) {
      this.currentConn.close();
    }
    this.currentConn = null;
  },
  isActive() {
    return Boolean(this.currentConn) && this.currentConn.readyState === WebSocket.OPEN;
  }
};

export function wsConnect() {
  internalConnect();
}

function internalConnect() {
  let token = getJwtToken();

  if (!token) {
    return;
  }

  // Create new websocket connection
  let webSocket = new WebSocket(WEB_SOCKET_ROOT + `?token=${token}`);

  // When connect successful
  webSocket.onopen = () => {
    webSocketManager.setNew(webSocket);

    // Function to send chat message
    let send = function (receiverId, message) {
      let msg = createMessage(messageType.CHAT, {
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

    if (type === messageType.CHAT) {
      // Handle chat
      store.dispatch(receiveMessage(data));
      console.log(data);
    } else if (type === messageType.SEND_BACK) {
      store.dispatch(sendbackMessage(data));
    } else if (type === messageType.ONLINE) {
      store.dispatch(onOffline(data, true));
    } else if (type === messageType.OFFLINE) {
      store.dispatch(onOffline(data, false));
    }
  };

  // Try to reconnect
  webSocket.onclose = () => {
    setTimeout(() => {
      if (!webSocketManager.isActive()) {
        console.log('reconnect');
        internalConnect(token);
      }
    }, 1000);
  };

  webSocket.onerror = () => {
    webSocketManager.clean();
  }
}
