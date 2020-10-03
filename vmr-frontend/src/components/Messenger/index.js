import React from 'react';
import LeftSideBar from '../LeftSideBar';
import './Messenger.css';
import MessageListWrapper from '../MessageListWrapper';
import AddFriendModal from "../AddFriendModal";

function Messenger() {
  return (
    <div className="scrollable messenger">

      <LeftSideBar/>

      <MessageListWrapper/>

      <AddFriendModal/>
    </div>
  );
}

export default Messenger;
