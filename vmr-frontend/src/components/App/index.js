import React from 'react';
import Messenger from '../Messenger';
import store from '../../redux/vmr-store';
import {Provider, useSelector} from 'react-redux';
import {BrowserRouter as Router} from 'react-router-dom';
import RegisterPage from "../RegisterPage";
import 'antd/dist/antd.css';
import LoginPage from "../LoginPage";

const {Switch, Route, Redirect} = require('react-router-dom');

function AuthFragment() {
  let authStatus = useSelector(state => state.user.jwt != null);
  if (authStatus) {
    return <Messenger/>;
  }
  return <Redirect to={"/login"}/>;
}

export default function App() {
  return (
    <Provider store={store}>
      <div className="App">
        <Router>
          <Switch>
            <Route path="/register" component={RegisterPage}/>
            <Route path="/login" component={LoginPage}/>
            <Route path="/" component={AuthFragment}/>
          </Switch>
        </Router>
      </div>
    </Provider>
  );
}
