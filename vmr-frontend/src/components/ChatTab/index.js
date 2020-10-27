import React, {useState} from "react";
import ConversationListItem from "../ConversationListItem";
import {useSelector} from "react-redux";
import FriendSearch from "../FriendSearch";

export default function ChatTab() {
  let friends = useSelector(state => state.friends.friends);
  let [fFriendsId, setFFriendsId] = useState([]);

  let filter = event => {
    let value = event.target.value.toLowerCase();
    setFFriendsId(Object.keys(friends).filter(x => {
      let friend = friends[x];
      return friend.name.toLowerCase().includes(value)
        || friend.username.toLowerCase().includes(value);
    }));
  };

  let getIds = () => {
    if (fFriendsId.length !== 0) {
      return fFriendsId;
    } else {
      return Object.keys(friends);
    }
  };

  return (
    <div className="conversation-list-scroll">
      <FriendSearch onFilter={filter}/>
      {
        getIds()
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
