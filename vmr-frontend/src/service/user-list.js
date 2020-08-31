import {protectedGet} from "./axios-wrapper";
import {updateConservationId, updateUserList} from "../redux/vmr-action";
import store from '../redux/vmr-store';

export function getUsers() {
  return new Promise((resolve, reject) => {
    protectedGet('/users').then(response => {
      let userList = response.data.userList;
      store.dispatch(updateUserList(userList));
      store.dispatch(updateConservationId(userList[0].id));
      resolve(userList);
    }).catch(error => {
      console.error(error);
      reject('Cannot get user list');
    });
  });
}
