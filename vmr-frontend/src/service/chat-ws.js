import {protectedGet} from "./axios-wrapper";
import store from '../redux/vmr-store';
import {webSocketConnected, receiveMessage, sendbackMessage, onOffline} from "../redux/vmr-action";

const wsRoot = process.env.REACT_APP_WS_ROOT;

let oldWs = null;

function createMessage(type, data) {
  return {
    type, data
  }
}

export function wsConnect() {
  // Current user id
  let senderId = Number(localStorage.getItem("userId"));

  // Return websocket promise
  return new Promise((resolve) => {
      protectedGet("/sockettoken").then(response => {
        let token = response.data.data.token;
        let webSocket = new WebSocket(wsRoot + `?token=${token}`);

        // When connect successful
        webSocket.onopen = () => {
          // Set old
          if (oldWs !== null) {
            oldWs.close();
          }
          oldWs = webSocket;

          // Function to send chat message
          let send = function (receiverId, message) {
            let msg = createMessage('CHAT', {
              receiverId, message
            });
            webSocket.send(JSON.stringify(msg));
          };

          // Notify to redux
          store.dispatch(webSocketConnected(webSocket, send));

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

          resolve(webSocket);
        };
      }).catch(error => {
        console.error(error);
      });
    }
  );
}
