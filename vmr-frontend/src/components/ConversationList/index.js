import React, {useEffect, useState} from 'react';
import ConversationSearch from '../ConversationSearch';
import ConversationListItem from '../ConversationListItem';
import ToolbarButton from '../ToolbarButton';
import {connect, useDispatch, useSelector} from 'react-redux';
import logout from "../../service/logout";
import {getUsers} from "../../service/user-list";
import {wsConnect} from "../../service/chat-ws";

import './ConversationList.css';
import MenuBar from "../MenuBar";
import {setTab} from "../../redux/vmr-action";

function ConversationList(props) {
  let currentUserId = Number(localStorage.getItem("userId"));

  let tab = useSelector(state => state.ui.currentTab);
  let dispatch = useDispatch();

  let setCurrentTab = (tab) => {
    dispatch(setTab(tab));
  }

  useEffect(() => {
    getUsers().then(() => {
      wsConnect();
    });
  }, []);

  let conversations = props.userList.map(item => {
    return {
      name: (currentUserId !== item.id) ? item.name : `Bạn: ${item.name}`,
      text: `@${item.username}`,
      id: item.id,
      online: item.online,
      isCurrentUser: currentUserId === item.id
    }
  });

  let menuItems = [
    {key: "general", icon: "ion-ios-contact"},
    {key: "chat", icon: "ion-ios-chatbubbles"},
    {key: "notitfication", icon: "ion-ios-notifications"}
  ]

  return (
    <div className="conversation-list">
      <MenuBar
        items={[
          ...menuItems.map(x => (
            <ToolbarButton {...x} onClick={() => setCurrentTab(x.key)} isCurrent={x.key === tab}/>
          )),
          // <ToolbarButton key="general" icon="ion-ios-contact" onClick={() => setCurrentTab('general')}/>,
          // <ToolbarButton key="chat" icon="ion-ios-chatbubbles" onClick={() => setCurrentTab('chat')}/>,
          // <ToolbarButton key="notification" icon="ion-ios-notifications"
          //                onClick={() => setCurrentTab('notification')}/>,
          <ToolbarButton key="logout" icon="ion-ios-log-out" onClick={logout}/>
        ]}
      />
      {
        tab === 'chat' &&
        <div className="conversation-list-scroll">
          <ConversationSearch/>
          {
            conversations.map(conversation =>
              <ConversationListItem
                key={conversation.id}
                data={conversation}
              />
            )
          }
        </div>
      }
      {
        tab === 'notification' &&
        <div className="conversation-list-scroll">

        </div>
      }
    </div>
  );
}

function mapStateToProps(state) {
  return {
    userList: state.users.userList
  }
}

export default connect(mapStateToProps, null)(ConversationList);
