import React from 'react';
import ToolbarButton from '../ToolbarButton';
import {useDispatch, useSelector} from 'react-redux';
import logout from "../../service/logout";
import './ConversationList.css';
import MenuBar from "../MenuBar";
import {setTab} from "../../redux/vmr-action";
import FriendTab from "../FriendTab";
import ChatTab from "../ChatTab";

export default function ConversationList() {
  let tab = useSelector(state => state.ui.currentTab);
  let dispatch = useDispatch();

  let setCurrentTab = (tab) => {
    dispatch(setTab(tab));
  }

  let menuItems = [
    {key: "general", icon: "ion-ios-contact"},
    {key: "chat", icon: "ion-ios-chatbubbles"},
    {key: "friend", icon: "ion-ios-contacts"},
    {key: "notitfication", icon: "ion-ios-notifications"}
  ]

  return (
    <div className="conversation-list">
      <MenuBar
        items={[
          ...menuItems.map(x => (
            <ToolbarButton {...x} onClick={() => setCurrentTab(x.key)} isCurrent={x.key === tab}/>
          )),
          <ToolbarButton key="logout" icon="ion-ios-log-out" onClick={logout}/>
        ]}
      />
      {
        tab === 'chat' && <ChatTab/>
      }
      {
        tab === 'friend' && <FriendTab/>
      }
    </div>
  );
}

