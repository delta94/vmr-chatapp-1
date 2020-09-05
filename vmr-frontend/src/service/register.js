import {post} from "./axios-wrapper";
import store from '../redux/vmr-store';
import {login} from "../redux/vmr-action";

export default function register(username, name, password) {
  return new Promise((resolve, reject) => {
    post('/register', {username, name, password}).then(response => {
      let data = response.data.data;

      // Save to local storage
      localStorage.setItem("jwtToken", data.jwtToken);
      localStorage.setItem("userId", data.userId);

      // Dispatch to store
      store.dispatch(login(data.jwtToken, data.userId));

      // Send to register page
      resolve(data);
    }).catch(error => {
      console.log(error);
      reject('Register failed');
    });
  })
}
