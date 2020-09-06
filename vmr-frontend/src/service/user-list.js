import {protectedGet} from "./axios-wrapper";
import {updateUserList} from "../redux/vmr-action";
import store from '../redux/vmr-store';

export function getUsers() {
  return new Promise((resolve) => {
    protectedGet('/users').then(response => {
      let userList = response.data.data.userList;
      store.dispatch(updateUserList(userList));
      resolve(userList);
    }).catch(error => {
      console.log(error);
    });
  });
}
