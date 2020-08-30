import {post} from "./axios-wrapper";
import store from '../redux/vmr-store';
import {login} from "../redux/vmr-action";

export default function register(username, name, password) {
  return new Promise((resolve, reject) => {
    post('/register', {username, name, password}).then(response => {
      // Debug status
      console.log('Register successfully');
      let data = response.data;
      console.log(data);

      // Dispatch to store
      store.dispatch(login(data.token, data.userId));

      // Save to local storage
      localStorage.setItem('jwtToken', data.token);
      localStorage.setItem('userId', data.userId);

      // Send to register page
      resolve(data);
    }).catch(error => {
      console.log(error);
      reject('Register failed');
    });
  })
}