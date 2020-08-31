import axios from 'axios';

const basePath = process.env.REACT_APP_API_ROOT;
const publicBasePath = basePath + '/api/public';
const protectedBasePath = basePath + '/api/protected';

export function post(path, data) {
  return axios.post(publicBasePath + path, data);
}

export function protectedPost(path, data) {
  let token = localStorage.getItem("jwtToken");
  return axios.post(protectedBasePath + path, data, {
    headers: {
      'Authorization': 'Bearer ' + token
    }
  });
}

export function protectedGet(path) {
  let token = localStorage.getItem("jwtToken");
  return axios.get(protectedBasePath + path, {
    headers: {
      'Authorization': 'Bearer ' + token
    }
  });
}