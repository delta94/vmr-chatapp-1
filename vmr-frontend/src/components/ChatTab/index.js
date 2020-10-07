import React from "react";
import ConversationListItem from "../ConversationListItem";
import {useSelector} from "react-redux";
import ConversationSearch from "../ConversationSearch";

export default function ChatTab() {
  let userList = useSelector(state => state.users.userList);

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
