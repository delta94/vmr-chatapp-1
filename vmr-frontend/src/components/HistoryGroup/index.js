import React from 'react';
import HistoryItem from "../HistoryItem";
import {getUserId} from "../../util/auth-util";

export default function HistoryGroup(props) {
  let userId = getUserId();
  let {month, items} = props;

  return (
    <div className={'history-group'}>
      <p>Tháng {month}</p>
      {
        items.sort((x, y) =>  y.id - x.id).map(x => (
        <HistoryItem
        type={x.type}
        friendId={((x.senderId === userId) ? x.receiverId : x.senderId)}
        amount={x.amount}
        timestamp={x.timestamp}
        key={x.id}
        message={x.message}
        />
        ))
      }
    </div>
  );
}