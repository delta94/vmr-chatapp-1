import {protectedGet} from "./axios-wrapper";
import store from '../redux/vmr-store';
import {webSocketConnected, receiveMessage, sendbackMessage, onOffline} from "../redux/vmr-action";

const WEB_SOCKET_ROOT = process.env.REACT_APP_WS_ROOT;

function createMessage(type, data) {
  return {
    type, data
  }
}

let webSocketActive = false;

export function wsConnect() {
  // log info
  console.log('Try to connecto to websocket');

  // Get socket token from server and create connection
  protectedGet("/sockettoken").then(response => {
    let token = response.data.data.token;
    internalConnect(token);
  }).catch(error => {
    console.error(error);
  });
}

function internalConnect(token) {
  // Get senderId
  let senderId = Number(localStorage.getItem("userId"));

  // Create new websocket connection
  let webSocket = new WebSocket(WEB_SOCKET_ROOT + `?token=${token}`);

  // When connect successful
  webSocket.onopen = () => {
    webSocketActive = true;

    // Function to send chat message
    let send = function (receiverId, message) {
      let msg = createMessage('CHAT', {
        receiverId, message
      });
      webSocket.send(JSON.stringify(msg));
    };

    // Notify to redux
    store.dispatch(webSocketConnected(webSocket, send));
  };

  // Handle chat message
  webSocket.onmessage = messageEvent => {
    let jsonMessage = JSON.parse(messageEvent.data);
    if (jsonMessage.type === 'CHAT') {
      // Handle chat
      store.dispatch(receiveMessage(jsonMessage.data));
    } else if (jsonMessage.type === 'SEND_BACK') {
      // Handle sendback
      if (jsonMessage.receiverId === senderId) {
        return;
      }
      store.dispatch(sendbackMessage(jsonMessage.data));
    } else if (jsonMessage.type === 'ONLINE') {
      store.dispatch(onOffline(jsonMessage.data, true));
    } else if (jsonMessage.type === 'OFFLINE') {
      store.dispatch(onOffline(jsonMessage.data, false));
    }
  };

  webSocket.onclose = () => {
    webSocketActive = false;
    setTimeout(() => {
      if (!webSocketActive) {
        internalConnect(token);
      }
    }, 1000);
  };

  webSocket.onerror = () => {
    webSocket.close();
  }
}
