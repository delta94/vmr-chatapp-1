import {post} from './axios-wrapper';
import {login} from "../redux/vmr-action";
import store from '../redux/vmr-store';

export function usernamePasswordLogin(username, password) {
  return new Promise((resolve, reject) => {
    post('/login', {username, password}).then(response => {
      // Debug status
      let {jwtToken, userId} = response.data.data;

      // Save to local storage
      localStorage.setItem("jwtToken", jwtToken);
      localStorage.setItem("userId", userId);

      // Dispatch to store
      store.dispatch(login(jwtToken, userId));

      // Send to login page
      resolve(response.data.data);
    }).catch(error => {
      reject(error.response.data.message);
    });
  });
}
