import React from 'react';
import {Avatar} from 'antd';
import {useDispatch, useSelector} from 'react-redux';
import './ConversationListItem.css';
import {setSideBarActive} from "../../redux/vmr-action";
import {getFirstLetter} from "../../util/string-util";
import {getColor} from "../../util/ui-util";
import {getUserId} from "../../util/auth-util";

const {useHistory} = require('react-router-dom');

export default function ConversationListItem(props) {
  let history = useHistory();
  let currentUserId = getUserId();
  let currentConversationId = useSelector(state => state.users.currentConversationId);
  let userMapHolder = useSelector(state => state.users.userMapHolder);
  let dispatch = useDispatch();

  let hideSideBar = () => {
    dispatch(setSideBarActive(false));
  };

  let itemStyle = {
    borderRadius: "5px",
    marginLeft: "10px",
    marginRight: "10px",
    border: '1px solid white'
  };

  let user = userMapHolder.userMap.get(props.friendId);
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

  if (currentConversationId === user.id) {
    itemStyle.backgroundColor = 'rgba(0, 0, 0, .05)';
    itemStyle.border = '1px solid #8fbee9';
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
    hideSideBar();
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
