import React from 'react';
import Messenger from '../Messenger';
import store from '../../redux/vmr-store';
import {Provider} from 'react-redux';
import {BrowserRouter as Router, Switch, Route, Redirect} from 'react-router-dom';
import RegisterPage from "../RegisterPage";
import 'antd/dist/antd.css';
import LoginPage from "../LoginPage";
import {connect} from "react-redux";

let AuthFragment = (props) => {
  if (props.authStatus) {
    return <Messenger />;
  }
  return <Redirect to={"/login"}/>;
};

function mapUserStatus(state) {
  return {
    authStatus: state.user.jwt !== null
  }
}

AuthFragment = connect(mapUserStatus, null)(AuthFragment);

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
