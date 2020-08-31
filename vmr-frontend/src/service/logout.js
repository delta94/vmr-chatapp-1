import store from '../redux/vmr-store';
import {logout} from "../redux/vmr-action";

export default function () {
  store.dispatch(logout());
  localStorage.clear();
  console.log("Logout");
}