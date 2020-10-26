import React from 'react';
import {Badge} from 'antd';
import {useDispatch, useSelector} from 'react-redux';
import './ConversationListItem.css';
import {clearNotifications, setSideBarActive} from "../../redux/vmr-action";
import {getUserId} from "../../util/auth-util";
import ChatAvatar from "../ChatAvatar";

const {useHistory} = require('react-router-dom');
const {FriendStatus} = require('../../proto/vmr/friend_pb');

export default function ConversationListItem(props) {
  let history = useHistory();
  let currentUserId = getUserId();
  let currentFriendId = useSelector(state => state.friends.currentFriendId);
  let friend = useSelector(state => state.friends.friends[props.friendId]);
  let dispatch = useDispatch();
  let {online, id: friendId} = friend;

  let hideSideBar = () => {
    dispatch(setSideBarActive(false));
  };

  let className = "conversation-list-item";
  let textClassName = "conversation-text";
  if (currentFriendId === friendId) {
    className += " current-friend";
  }
  if (friend.numNotifications !== 0) {
    textClassName += " unread";
  }

  let textMsg = `@${friend.username}`;
  if (friend.lastMsg) {
    if (friend.lastMsgType === 'TRANSFER') {
      if (friend.lastMsgSender !== currentUserId) {
        textMsg = friend.name + ' đã chuyển tiền cho bạn';
      } else {
        textMsg = 'Bạn đã chuyển tiền cho ' + friend.name;
      }
      if (textMsg.length > 30) {
        textMsg = textMsg.substr(0, 27) + '...';
      }
    } else {
      textMsg = ((friend.lastMsgSender === currentUserId) ? 'Bạn: ' : '') + friend.lastMsg;
      if (textMsg.length > 30) {
        textMsg = textMsg.substr(0, 27) + '...';
      }
    }
  }

  let clickHandle = () => {
    history.push('/t/' + friendId);
    dispatch(clearNotifications(friendId));
    hideSideBar();
  };

  return (
    <div className={className} onClick={clickHandle}>
      <ChatAvatar name={friend.name} onlineStatus={online && friend.status === FriendStatus.FRIEND}/>
      <div className="conversation-info" style={{paddingLeft: "10px"}}>
        <h1 className="conversation-title">{friend.name} <Badge size="small" count={friend.numNotifications}/></h1>
        <p className={textClassName}>{textMsg}</p>
      </div>
    </div>
  );
}
