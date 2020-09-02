import {protectedGet} from "./axios-wrapper";
import {updateUserList} from "../redux/vmr-action";
import store from '../redux/vmr-store';

export function getUsers() {
  return new Promise((resolve) => {
    console.log("Get user list");
    protectedGet('/users').then(response => {
      let userList = response.data.userList;
      console.log(userList);
      store.dispatch(updateUserList(userList));
      resolve(userList);
    }).catch(error => {
      console.log(error);
    });
  });
}
