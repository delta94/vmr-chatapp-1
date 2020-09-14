import {post} from "./axios-wrapper";
import store from '../redux/vmr-store';
import {login} from "../redux/vmr-action";

export default function register(username, name, password) {
  return new Promise((resolve, reject) => {
    post('/register', {username, name, password}).then(response => {
      let {jwtToken, userId} = response.data.data;

      // Save to local storage
      localStorage.setItem("jwtToken", jwtToken);
      localStorage.setItem("userId", userId);

      // Dispatch to store
      store.dispatch(login(jwtToken, userId));

      // Send to register page
      resolve(response.data.data);
    }).catch(error => {
      console.log(error);
      reject('Register failed');
    });
  })
}
