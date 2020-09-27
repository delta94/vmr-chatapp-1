import React, {useEffect} from 'react';
import ToolbarButton from '../ToolbarButton';
import {connect, useDispatch, useSelector} from 'react-redux';
import logout from "../../service/logout";
import {wsConnect} from "../../service/chat-ws";
import './ConversationList.css';
import MenuBar from "../MenuBar";
import {setTab, updateUserList} from "../../redux/vmr-action";
import FriendTab from "../FriendTab";
import ChatTab from "../ChatTab";
import {getChatFriendList} from "../../service/friend";

function ConversationList(props) {
  let currentUserId = Number(localStorage.getItem("userId"));

  let tab = useSelector(state => state.ui.currentTab);
  let friendReloadFlag = useSelector(state => state.ui.friendReloadFlag);
  let dispatch = useDispatch();

  let setCurrentTab = (tab) => {
    dispatch(setTab(tab));
  }

  useEffect(() => {
    getChatFriendList().then(result => {
      console.log(result.getFriendinfoList().length);
      dispatch(updateUserList(result.getFriendinfoList().map(x => {
        console.log(x.getOnline());
        return {
          id: x.getId(),
          name: x.getName(),
          username: x.getUsername(),
          online: x.getOnline()
        }
      })));
      wsConnect();
    }).catch(err => {
      console.log(err);
    });
  }, [friendReloadFlag]);

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
    {key: "friend", icon: "ion-ios-contacts"},
    {key: "notitfication", icon: "ion-ios-notifications"}
  ]

  return (
    <div className="conversation-list">
      <MenuBar
        items={[
          ...menuItems.map(x => (
            <ToolbarButton {...x} onClick={() => setCurrentTab(x.key)} isCurrent={x.key === tab}/>
          )),
          <ToolbarButton key="logout" icon="ion-ios-log-out" onClick={logout}/>
        ]}
      />
      {
        tab === 'chat' && <ChatTab conversations={conversations}/>
      }
      {
        tab === 'friend' && <FriendTab/>
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
