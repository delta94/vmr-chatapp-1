import React from 'react';
import HistoryItem from "../HistoryItem";

export default function HistoryGroup() {
  return (
    <div className={'history-group'}>
      <p>Tháng 10/2020</p>
      <HistoryItem type={'transfer'}/>
      <HistoryItem type={'receive'}/>
      <HistoryItem type={'transfer'}/>
    </div>
  );
}