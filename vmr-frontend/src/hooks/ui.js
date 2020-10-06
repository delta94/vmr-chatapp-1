import {useDispatch} from "react-redux";
import {setSideBarActive} from "../redux/vmr-action";

export function useOpenSideBar() {
  let dispatch = useDispatch();
  return function () {
    dispatch(setSideBarActive(true));
  };
}
