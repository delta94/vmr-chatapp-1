import {protectedGet} from "./axios-wrapper";

const wsRoot = process.env.REACT_APP_WS_ROOT;

export function wsConnect() {
  return new Promise((resolve, reject) => {
    protectedGet("/sockettoken").then(response => {
      let token = response.data.token;
      let webSocket = new WebSocket(wsRoot + `?token=${token}`);
      resolve(webSocket);
    }).catch(error => {
      console.log(error);
      reject(error);
    });
  });
}
