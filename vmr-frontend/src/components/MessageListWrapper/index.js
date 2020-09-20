import React from 'react';
import MessageList from '../MessageList';
const {Switch, Route} =  require('react-router-dom');

export default function MessageListWrapper() {
  return (
    <Switch>
      <Route path="/t/:receiverId" component={MessageList}/>
      <Route exact path="/">
        <div/>
      </Route>
    </Switch>
  );
}
