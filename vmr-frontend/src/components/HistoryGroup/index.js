import React from 'react';
import HistoryItem from "../HistoryItem";
import moment from "moment";

export default function HistoryGroup() {
  console.log(moment().seconds());
  return (
    <div className={'history-group'}>
      <p>Tháng 10/2020</p>
      <HistoryItem
        type={'transfer'}
        friendName={'Nguyễn Văn A'}
        amount={10000}
        timestamp={moment().unix()}
        message={'Chuyển tiền'}/>
      <HistoryItem
        type={'receive'}
        friendName={'Nguyễn Văn A'}
        timestamp={moment().unix()}
        amount={27000}
        message={'Chuyển tiền'}/>
      <HistoryItem
        type={'transfer'}
        friendName={'Nguyễn Văn A'}
        amount={1245}
        timestamp={moment().unix()}
        message={'Chuyển tiền'}/>
    </div>
  );
}