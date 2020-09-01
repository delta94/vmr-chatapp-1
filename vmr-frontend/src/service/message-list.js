import {protectedGet} from "./axios-wrapper";

export function getMessageList(friendId, offset) {
  return new Promise(resolve => {
    let path = `/chat/${friendId}/${offset}`
    protectedGet(path).then(result => {
      let data = result.data;
      console.log(data);
      resolve(data);
    });
  });
}
