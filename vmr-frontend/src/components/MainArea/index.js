import React from 'react';
import MessageList from '../MessageList';
import BalancePage from '../BalancePage';
import HistoryPage from "../HistoryPage";
import EmptyPage from "../EmptyPage";

const {Switch, Route} = require('react-router-dom');

export default function MainArea() {
  return (
    <Switch>
      <Route path="/t/:receiverId" component={MessageList}/>
      <Route path="/w/balance" component={BalancePage}/>
      <Route path="/w/history" component={HistoryPage}/>
      <Route exact path="/">
        <EmptyPage/>
      </Route>
    </Switch>
  );
}
