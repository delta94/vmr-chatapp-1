import {protectedGet} from "./axios-wrapper";

export function getMessageList(friendId, offset) {
  console.log('Get message, offset=', offset);
  return new Promise(resolve => {
    let path = `/chat/${friendId}/${offset}`
    protectedGet(path).then(result => {
      let data = result.data.data;
      resolve(data);
    }).catch(error => {
      console.log(error);
    });
  });
}
