import React, {useEffect} from 'react';
import ConversationSearch from '../ConversationSearch';
import ConversationListItem from '../ConversationListItem';
import Toolbar from '../Toolbar';
import ToolbarButton from '../ToolbarButton';
import {connect} from 'react-redux';

import './ConversationList.css';
import {getUsers} from "../../service/user-list";

function ConversationList(props) {
  useEffect(() => {
    getUsers();
  }, []);

  console.log('COnservation list', props);

  let conversations = props.userList.map(item => {
    return {
      name: item.name,
      text: `@${item.username}`,
      id: item.id
    }
  });

  let handleClick = (event) => {

  };

  return (
    <div className="conversation-list">
      <Toolbar
        title="Messenger"
        leftItems={[
          <ToolbarButton key="cog" icon="ion-ios-cog"/>
        ]}
        rightItems={[
          <ToolbarButton key="add" icon="ion-ios-add-circle-outline"/>
        ]}
      />
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
  );
}

function mapStateToProps(state) {
  console.log(state);
  console.log(state.userList);
  return {
    userList: state.userList
  }
}

export default connect(mapStateToProps, null)(ConversationList);