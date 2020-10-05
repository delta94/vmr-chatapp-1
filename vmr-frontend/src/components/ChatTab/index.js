import React, {useEffect} from "react";
import ConversationListItem from "../ConversationListItem";
import {useSelector} from "react-redux";
import {getChatFriendList} from "../../service/friend";
import {updateUserList} from "../../redux/vmr-action";
import {wsConnect} from "../../service/chat-ws";
import {useDispatch} from "react-redux";
import ConversationSearch from "../ConversationSearch";

export default function ChatTab() {
  let dispatch = useDispatch();
  let friendReloadFlag = useSelector(state => state.ui.friendReloadFlag);
  let userList = useSelector(state => state.users.userList);

  useEffect(() => {
    getChatFriendList().then(result => {
      dispatch(updateUserList(result.getFriendinfoList().map(x => {
        return {
          id: x.getId(),
          name: x.getName(),
          username: x.getUsername(),
          online: x.getOnline(),
          lastMsg: x.getLastMessage(),
          lastMsgSender: x.getLastMessageSender()
        }
      })));

      // Connect to websocket
      wsConnect();
    }).catch(err => {
      console.log(err);
    });
  }, [friendReloadFlag, dispatch]);

  return (
    <div className="conversation-list-scroll">
      <ConversationSearch/>
      {
        userList.map(user =>
          <ConversationListItem
            key={user.id}
            friendId={user.id}
          />
        )
      }
    </div>
  );
}
