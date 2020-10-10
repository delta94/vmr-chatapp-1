import React from 'react';
import LeftSideBar from '../LeftSideBar';
import MainArea from '../MainArea';
import AddFriendModal from "../AddFriendModal";

import './Messenger.css';

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
