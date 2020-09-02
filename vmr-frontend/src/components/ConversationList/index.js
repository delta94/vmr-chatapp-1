import React, {useEffect} from 'react';
import ConversationSearch from '../ConversationSearch';
import ConversationListItem from '../ConversationListItem';
import Toolbar from '../Toolbar';
import ToolbarButton from '../ToolbarButton';
import {connect} from 'react-redux';
import logout from "../../service/logout";
import {getUsers} from "../../service/user-list";
import {wsConnect} from "../../service/chat-ws";

import './ConversationList.css';

function ConversationList(props) {
  let currentUserId = Number(localStorage.getItem("userId"));

  useEffect(() => {
    getUsers().then(() => {
      console.log('Ws connect');
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

  return (
    <div className="conversation-list">
      <Toolbar
        title="Messenger"
        leftItems={[
          <ToolbarButton key="cog" icon="ion-ios-cog"/>
        ]}
        rightItems={[
          <ToolbarButton key="add" icon="ion-ios-log-out" onClick={logout}/>
        ]}
      />
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
    </div>
  );
}

function mapStateToProps(state) {
  return {
    userList: state.userList
  }
}

export default connect(mapStateToProps, null)(ConversationList);
