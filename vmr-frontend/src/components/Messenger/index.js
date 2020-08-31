import React from 'react';
import ConversationList from '../ConversationList';
import MessageList from '../MessageList';
import './Messenger.css';
import {connect} from 'react-redux';

function mapStateToProps(state) {
  return {
    currentConversationId: state.currentConversationId
  }
}

function Messenger(props) {
  return (
    <div className="messenger">

      <div className="scrollable sidebar">
        <ConversationList/>
      </div>

      <div className="scrollable content">
        <MessageList/>
      </div>
    </div>
  );
}

export default connect(mapStateToProps, null)(Messenger);