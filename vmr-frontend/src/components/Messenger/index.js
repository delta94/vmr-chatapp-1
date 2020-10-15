import React from 'react';
import LeftSideBar from '../LeftSideBar';
import MainArea from '../MainArea';

import './Messenger.css';

function Messenger() {
  return (
    <div className="scrollable messenger">
      <LeftSideBar/>
      <MainArea/>
    </div>
  );
}

export default Messenger;
