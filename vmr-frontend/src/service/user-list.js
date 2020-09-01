import {protectedGet} from "./axios-wrapper";
import {updateUserList} from "../redux/vmr-action";
import store from '../redux/vmr-store';

export function getUsers() {
  console.log('Get user');
  return new Promise((resolve) => {
    protectedGet('/users').then(response => {
      let userList = response.data.userList;
      store.dispatch(updateUserList(userList));
      resolve(userList);
    }).catch(error => {
      console.error(error);
    });
  });
}
