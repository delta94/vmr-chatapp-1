import React, {useEffect, useState} from 'react';

import TitleBar from '../TitleBar';

import './HistoryPage.css';
import HistoryGroup from "../HistoryGroup";
import {getHistory} from "../../service/wallet";

export default function HistoryPage() {
  useEffect(() => {
    getHistory().then(result => {
      console.log(result)
    }).catch(error => {
      console.log(error);
    });
  }, []);
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
