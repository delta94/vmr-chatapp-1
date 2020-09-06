import {createStore, combineReducers} from 'redux';
import userReducer from "./user-reducer";
import appReducer from "./vmr-reducer";
import userListReducer from "./user-list-reducer";
import chatListReducer from "./chat-list-reducer";

let reducer = combineReducers({
  user: userReducer,
  app: appReducer,
  users: userListReducer,
  chat: chatListReducer
})

export default createStore(reducer);
