import React from 'react';
import LeftSideBar from '../LeftSideBar';
import './Messenger.css';
import MainArea from '../MainArea';
import AddFriendModal from "../AddFriendModal";

function Messenger() {
  return (
    <div className="scrollable messenger">
      <LeftSideBar/>
      <MainArea/>
      <AddFriendModal/>
    </div>
  );
}

export default Messenger;
