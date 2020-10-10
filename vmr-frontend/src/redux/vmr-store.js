import {createStore, combineReducers} from 'redux';
import userReducer from "./user-reducer";
import websocketReducer from "./websocket-reducer";
import friendReducer from "./friend-reducer";
import messageReducer from "./message-reducer";
import uiReducer from "./ui-reducer";

let reducer = combineReducers({
  user: userReducer,
  webSocket: websocketReducer,
  friends: friendReducer,
  chat: messageReducer,
  ui: uiReducer
});

export default createStore(
  reducer, window['__REDUX_DEVTOOLS_EXTENSION__'] && window['__REDUX_DEVTOOLS_EXTENSION__']());
