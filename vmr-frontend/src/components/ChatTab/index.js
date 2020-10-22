import React from "react";
import ConversationListItem from "../ConversationListItem";
import {useSelector} from "react-redux";
import ConversationSearch from "../FriendSearch";

export default function ChatTab() {
  let friends = useSelector(state => state.friends.friends);

  return (
    <div className="conversation-list-scroll">
      <ConversationSearch/>
      {
        Object.keys(friends)
          .sort((x, y) => friends[y].lastMsgTimestamp - friends[x].lastMsgTimestamp)
          .map(x =>
            <ConversationListItem
              key={x}
              friendId={x}
            />
          )
      }
    </div>
  );
}
