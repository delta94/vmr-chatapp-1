import {createStore, combineReducers} from 'redux';
import userReducer from "./user-reducer";
import websocketReducer from "./websocket-reducer";
import userListReducer from "./user-list-reducer";
import chatListReducer from "./chat-list-reducer";
import uiReducer from "./ui-reducer";

let reducer = combineReducers({
  user: userReducer,
  webSocket: websocketReducer,
  users: userListReducer,
  chat: chatListReducer,
  ui: uiReducer
});

export default createStore(
  reducer,
  window["__REDUX_DEVTOOLS_EXTENSION__"]
  && window["__REDUX_DEVTOOLS_EXTENSION__()"]);
