import React from 'react';
import MessageList from '../MessageList';
import {Switch, Route} from 'react-router-dom';

export default function MessageListWrapper() {
  return (
    <Switch>
      <Route path="/t/:receiverId" component={MessageList} />
      <Route exact path="/">
        <div />
      </Route>
    </Switch>
  );
}
