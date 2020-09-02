import {post} from './axios-wrapper';
import {login} from "../redux/vmr-action";
import store from '../redux/vmr-store';

export function usernamePasswordLogin(username, password) {
  return new Promise((resolve, reject) => {
    post('/login', {username, password}).then(response => {
      // Debug status
      let data = response.data;

      // Save to local storage
      localStorage.setItem("jwtToken", data.jwtToken);
      localStorage.setItem("userId", data.userId);

      // Dispatch to store
      store.dispatch(login(data.jwtToken, data.userId));

      // Send to login page
      resolve(data);
    }).catch(reason => {
      console.error(reason);
      reject('Login failed');
    });
  });
}