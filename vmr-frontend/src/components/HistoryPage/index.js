import React from 'react';

import TitleBar from '../TitleBar';

import './HistoryPage.css';
import HistoryGroup from "../HistoryGroup";

export default function HistoryPage() {
  return (
    <div className={'history-page'}>
      <TitleBar title={'Lịch sử'}/>
      <div className={'history-list'}>
        <HistoryGroup/>
        <HistoryGroup/>
        <HistoryGroup/>
      </div>
    </div>
  );
}
