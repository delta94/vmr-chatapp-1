import React from 'react';
import {useSelector} from 'react-redux';
import './LeftSideBar.css';
import MenuBar from "../MenuBar";
import FriendTab from "../FriendTab";
import ChatTab from "../ChatTab";

export default function ConversationList() {
  let tab = useSelector(state => state.ui.currentTab);

  return (
    <div className="conversation-list">
      <MenuBar/>
      {tab === 'chat' && <ChatTab/>}
      {tab === 'friend' && <FriendTab/>}
    </div>
  );
}

