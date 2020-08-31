import {post} from './axios-wrapper';
import {login} from "../redux/vmr-action";
import store from '../redux/vmr-store';

export function usernamePasswordLogin(username, password) {
  return new Promise((resolve, reject) => {
    post('/login', {username, password}).then(response => {
      // Debug status
      console.log('Login success');
      let data = response.data;
      console.log(data);

      // Dispatch to store
      store.dispatch(login(data.jwtToken, data.userId));

      // Save to local storage
      localStorage.setItem("jwtToken", data.jwtToken);
      localStorage.setItem("userId", data.userId);

      console.log(localStorage);
      // Send to login page
      resolve(data);
    }).catch(reason => {
      console.log(reason);
      reject('Login failed');
    });
  });
}