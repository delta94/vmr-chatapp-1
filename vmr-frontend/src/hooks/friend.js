import {getChatFriendList} from "../service/friend";
import {updateFriendList} from "../redux/vmr-action";
import {wsConnect} from "../service/chat-ws";
import {useDispatch, useSelector} from "react-redux";
import {useEffect} from "react";

export function useFriendList() {
  let dispatch = useDispatch();
  let friendReloadFlag = useSelector(state => state.ui.friendReloadFlag);

  useEffect(() => {
    getChatFriendList().then(result => {
      dispatch(updateFriendList(result.getFriendInfoList().map(x => {
        return {
          id: x.getId(),
          name: x.getName(),
          username: x.getUsername(),
          online: x.getOnline(),
          lastMsg: x.getLastMessage(),
          lastMsgSender: x.getLastMessageSender(),
          lastMsgType: x.getLastMessageType(),
          lastMsgTimestamp: x.getLastMessageTimestamp(),
          numNotifications: x.getNumUnreadMessage(),
          status: x.getFriendStatus()
        }
      })));

      // Connect to websocket
      wsConnect();
    }).catch(err => {
      console.log(err);
    });
  }, [friendReloadFlag, dispatch]);
}
