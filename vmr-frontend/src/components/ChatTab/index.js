
import React, {useEffect} from "react";
import ConversationListItem from "../ConversationListItem";
import {getUserInfo} from "../../service/query-user";
import {getUserId} from "../../util/auth-util";
import {useSelector} from "react-redux";
import {getChatFriendList} from "../../service/friend";
import {updateUserList} from "../../redux/vmr-action";
import {wsConnect} from "../../service/chat-ws";
import {useDispatch} from "react-redux";

export default function ChatTab(props) {
  let currentUserId = getUserId();
  let dispatch = useDispatch();
  let friendReloadFlag = useSelector(state => state.ui.friendReloadFlag);
  let userList = useSelector(state => state.users.userList);

  useEffect(() => {
    getUserInfo().then(res => {
      console.log(res);
    }).catch(err => {
      console.log(err);
    });
  }, []);

  useEffect(() => {
    getChatFriendList().then(result => {
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

  let conversations = userList.map(item => {
    return {
      name: (currentUserId !== item.id) ? item.name : `Bạn: ${item.name}`,
      text: `@${item.username}`,
      id: item.id,
      online: item.online,
      isCurrentUser: currentUserId === item.id
    }
  });

  return (
    <div className="conversation-list-scroll">
      {/*<ConversationListItem key={}/>*/}
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
