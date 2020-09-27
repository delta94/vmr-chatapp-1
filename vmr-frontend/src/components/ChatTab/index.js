import ConversationListItem from "../ConversationListItem";
import React from "react";

export default function ChatTab(props) {
  let {conversations} = props;
  return (
    <div className="conversation-list-scroll">
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
