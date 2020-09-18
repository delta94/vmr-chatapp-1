import store from '../redux/vmr-store';
import {logout} from "../redux/vmr-action";
import {logout as localStorageLogout} from '../util/auth-util';

export default function () {
  store.dispatch(logout());
  localStorageLogout();
}