import axios from 'axios';

const BASE_PATH = process.env.REACT_APP_API_ROOT;
const PUBLIC_BASE_PATH = BASE_PATH + '/api/public';
const PROTECTED_BASE_PATH = BASE_PATH + '/api/protected';

export function post(path, data) {
  return axios.post(PUBLIC_BASE_PATH + path, data);
}

export function protectedPost(path, data) {
  let token = localStorage.getItem("jwtToken");
  return axios.post(PROTECTED_BASE_PATH + path, data, {
    headers: {
      'Authorization': 'Bearer ' + token
    }
  });
}

export function protectedGet(path) {
  let token = localStorage.getItem("jwtToken");
  return axios.get(PROTECTED_BASE_PATH + path, {
    headers: {
      'Authorization': 'Bearer ' + token
    }
  });
}
