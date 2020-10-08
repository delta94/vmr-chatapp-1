import React from "react";
import ConversationListItem from "../ConversationListItem";
import {useSelector} from "react-redux";
import ConversationSearch from "../ConversationSearch";

export default function ChatTab() {
  let friendList = useSelector(state => state.friends.friends);

  return (
    <div className="conversation-list-scroll">
      <ConversationSearch/>
      {
        Object.keys(friendList).map(x =>
          <ConversationListItem
            key={x}
            friendId={x}
          />
        )
      }
    </div>
  );
}
