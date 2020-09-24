import React from 'react';
import ConversationList from '../ConversationList';
import './Messenger.css';
import {connect} from 'react-redux';
import MessageListWrapper from '../MessageListWrapper';
import AddFriendModal from "../AddFriendModal";

function mapStateToProps(state) {
  return {
    sideBarActive: state.ui.sideBarActive
  }
}

function Messenger(props) {
  let {sideBarActive} = props;

  let sideBarClassName = "sidebar ";
  if (window.innerWidth <= 700 && sideBarActive) {
    sideBarClassName  += "sidebar-active";
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

export default connect(mapStateToProps, null)(Messenger);
