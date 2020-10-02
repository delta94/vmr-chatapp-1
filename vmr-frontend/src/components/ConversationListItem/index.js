import React from 'react';
import {Avatar} from 'antd';
import {connect} from 'react-redux';
import './ConversationListItem.css';
import {setSideBarActive} from "../../redux/vmr-action";
import {getFirstLetter} from "../../util/string-util";
import {getColor} from "../../util/ui-util";
import {getUserId} from "../../util/auth-util";

const {useHistory} = require('react-router-dom');

function ConversationListItem(props) {
  let history = useHistory();

  let currentUserId = getUserId();

  let itemStyle = {
    borderRadius: "5px",
    marginLeft: "10px",
    marginRight: "10px"
  };

  let user = props.userMapHolder.userMap.get(props.friendId);
  let {online, id} = user;
  let isCurrentUser = id === currentUserId;

  let onlineStyle = "dot";
  if (isCurrentUser) {
    onlineStyle = "dot current-user"
  } else if (online) {
    onlineStyle = "dot online";
  }

  let avatarStyle = {
    backgroundColor: getColor(user.id)
  }

  if (props.currentConversationId === user.id) {
    itemStyle.backgroundColor = 'rgba(0, 0, 0, .05)';
  }

  let textMsg = `@${user.username}`;
  if (user.lastMsg) {
    textMsg = ((user.lastMsgSender === currentUserId) ? 'Bạn: ' : '') + user.lastMsg;
    if (textMsg.length > 30) {
      textMsg = textMsg.substr(0, 27) + '...';
    }
  }

  let clickHandle = () => {
    history.push('/t/' + id);
    props.hideSideBar();
  }

  return (
    <div className="conversation-list-item" onClick={clickHandle} style={itemStyle}>
      <Avatar style={avatarStyle} size={50}>
        {getFirstLetter(user.name)}
      </Avatar>
      <div className="conversation-info" style={{paddingLeft: "10px"}}>
        <h1 className="conversation-title"><span className={onlineStyle}/>{user.name}</h1>
        <p className="conservation-text" style={{marginBottom: 0, color: '#888'}}>{textMsg}</p>
      </div>
    </div>
  );
}

function mapStateToProps(state) {
  console.log('Updated state');
  return {
    currentConversationId: state.users.currentConversationId,
    userMapHolder: state.users.userMapHolder
  }
}

function mapDispatchToProps(dispatch) {
  return {
    hideSideBar: () => {
      dispatch(setSideBarActive(false));
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(ConversationListItem);
