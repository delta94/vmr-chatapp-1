import ConversationListItem from "../ConversationListItem";
import React, {useEffect} from "react";
import {getUserInfo} from "../../service/query-user";

export default function ChatTab(props) {
  let {conversations} = props;

  useEffect(() => {
    getUserInfo().then(res => {
      console.log(res);
    }).catch(err => {
      console.log(err);
    });
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
