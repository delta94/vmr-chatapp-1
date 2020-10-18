import {post, protectedPost} from './axios-wrapper';
import {login as loginAction, logout as logoutAction} from "../redux/vmr-action";
import store from '../redux/vmr-store';
import {jwtLogin, logout as localStorageLogout} from "../util/auth-util";

export function login(username, password) {
  return new Promise((resolve, reject) => {
    post('/login', {username, password}).then(response => {
      // Debug status
      let {jwtToken, userId} = response.data.data;

      // Save to local storage
      jwtLogin(jwtToken, userId);

      // Dispatch to store
      store.dispatch(loginAction(jwtToken, userId));

      // Send to login page
      resolve(response.data.data);
    }).catch(error => {
      // Get error code
      reject(error.response.data["responseCode"]);
    });
  });
}

export function logout() {
  protectedPost("/logout").then((res) => {
    console.log(res.data.data);
    localStorageLogout();
    store.dispatch(logoutAction());
  }).catch(err => {
    console.log(err);
  });
}

export function register(username, name, password) {
  return new Promise((resolve, reject) => {
    post('/register', {username, name, password}).then(response => {
      let {jwtToken, userId} = response.data.data;

      // Save to local storage
      jwtLogin(jwtToken, userId);

      // Dispatch to store
      store.dispatch(loginAction(jwtToken, userId));

      // Send to register page
      resolve(response.data.data);
    }).catch(error => {
      console.log(error);
      reject(error.response.data);
    });
  })
}
