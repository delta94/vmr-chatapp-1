import axios from 'axios';

const basePath = process.env.REACT_APP_API_ROOT;
const publicBasePath = basePath + '/api/public';

export function post(path, data) {
  return axios.post(publicBasePath + path, data);
}