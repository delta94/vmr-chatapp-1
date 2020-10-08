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
  let currentFriendId = useSelector(state => state.friends.currentFriendId);
  let user = useSelector(state => state.friends.friends[props.friendId]);
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

  let {online, id} = user;

  let avatarStyle = {
    backgroundColor: getColor(user.id)
  }

  if (currentFriendId === user.id) {
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
      <div className="avatar">
        <Avatar style={avatarStyle} size={50}>
          {getFirstLetter(user.name)}
        </Avatar>
        {online && <span className={"badge "}/>}
      </div>
      <div className="conversation-info" style={{paddingLeft: "10px"}}>
        <h1 className="conversation-title">{user.name}</h1>
        <p className="conservation-text" style={{marginBottom: 0, color: '#888'}}>{textMsg}</p>
      </div>
    </div>
  );
}
