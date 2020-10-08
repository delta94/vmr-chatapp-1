import React, {Suspense} from 'react';
import Messenger from '../Messenger';
import store from '../../redux/vmr-store';
import {Provider, useSelector} from 'react-redux';
import {BrowserRouter as Router} from 'react-router-dom';

import 'antd/dist/antd.css';

const RegisterPage = React.lazy(() => import('../RegisterPage'));
const LoginPage = React.lazy(() => import('../LoginPage'));

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
          <Suspense fallback={<div/>}>
            <Switch>
              <Route path="/register" component={RegisterPage}/>
              <Route path="/login" component={LoginPage}/>
              <Route path="/" component={AuthFragment}/>
            </Switch>
          </Suspense>
        </Router>
      </div>
    </Provider>
  );
}
