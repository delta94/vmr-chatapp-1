import React from 'react';
import ConversationList from '../LeftSideBar';
import './Messenger.css';
import {useSelector} from 'react-redux';
import MessageListWrapper from '../MessageListWrapper';
import AddFriendModal from "../AddFriendModal";

function Messenger() {
  let sideBarActive = useSelector(state => state.ui.sideBarActive);

  let sideBarClassName = "sidebar ";
  if (window.innerWidth <= 700 && sideBarActive) {
    sideBarClassName += "sidebar-active";
  }

  return (
    <div className="scrollable messenger">

      <div className={sideBarClassName}>
        <ConversationList/>
      </div>

      <div className="scrollable content">
        <MessageListWrapper/>
      </div>

      <AddFriendModal/>
    </div>
  );
}

export default Messenger;
