import React from 'react';
import ConversationList from '../ConversationList';
import './Messenger.css';
import {connect} from 'react-redux';
import MessageListWrapper from '../MessageListWrapper';

function mapStateToProps(state) {
  return {
    currentConversationId: state.currentConversationId
  }
}

function Messenger() {
  return (
    <div className="scrollable messenger">

      <div className="sidebar">
        <ConversationList/>
      </div>

      <div className="scrollable content">
        <MessageListWrapper/>
      </div>
    </div>
  );
}

export default connect(mapStateToProps, null)(Messenger);
