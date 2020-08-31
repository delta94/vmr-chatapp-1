import {protectedGet} from "./axios-wrapper";
import store from '../redux/vmr-store';
import {webSocketConnected, receiveMessage} from "../redux/vmr-action";

const wsRoot = process.env.REACT_APP_WS_ROOT;
let ws = null;

export function wsConnect() {
  // Current user id
  let senderId = Number(localStorage.getItem("userId"));

  // Return websocket promise
  return new Promise((resolve, reject) => {
      if (ws === null) {
        protectedGet("/sockettoken").then(response => {
          let token = response.data.token;
          let webSocket = new WebSocket(wsRoot + `?token=${token}`);

          // When connect successful
          webSocket.onopen = event => {
            ws = webSocket;

            // Function to send chat message
            let send = function (receiverId, message) {
              let msg = {
                receiverId, message, type: 'CHAT'
              };
              if (receiverId !== senderId) {
                store.dispatch(receiveMessage({
                  senderId: receiverId,
                  receiverId: receiverId,
                  message: message,
                  timestamp: new Date().getTime() / 1000,
                  isMine: true
                }));
              }
              webSocket.send(JSON.stringify(msg));
            };

            // Test message
            send(senderId, "Hello world");

            // Notify to redux
            store.dispatch(webSocketConnected(webSocket, send));

            // Handle chat message
            webSocket.onmessage = messageEvent => {
              let jsonMessage = JSON.parse(messageEvent.data);
              jsonMessage.isMine = jsonMessage.senderId === senderId;
              store.dispatch(receiveMessage(jsonMessage));
            };
          };

          resolve(webSocket);
        }).catch(error => {
          console.log(error);
          reject(error);
        });
      }
    }
  );
}
