import React from 'react';
import MessageList from '../MessageList';

const {Switch, Route} = require('react-router-dom');

export default function MainArea() {
  return (
    <Switch>
      <Route path="/t/:receiverId" component={MessageList}/>
      <Route path="/w/balance">
        <div/>
      </Route>
      <Route path="/w/history">
        <div/>
      </Route>
      <Route exact path="/">
        <div/>
      </Route>
    </Switch>
  );
}
