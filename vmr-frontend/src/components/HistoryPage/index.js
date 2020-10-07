import React, {useEffect, useState} from 'react';

import TitleBar from '../TitleBar';

import './HistoryPage.css';
import HistoryGroup from "../HistoryGroup";
import {getHistory} from "../../service/wallet";
import {timestampSecond2Month} from "../../util/string-util";

export default function HistoryPage() {
  let [historyMonth, setHistoryMonth] = useState({});

  useEffect(() => {
    getHistory().then(result => {
      let newHistory = {};

      for (let item of result.reverse()) {
        let month = timestampSecond2Month(item.getTimestamp());

        let historyItem = {
          senderId: item.getSender(),
          receiverId: item.getReceiver(),
          timestamp: item.getTimestamp(),
          amount: item.getAmount(),
          message: item.getMessage(),
          type: item.getType()
        }

        if (newHistory[timestampSecond2Month(item.getTimestamp())] === undefined) {
          newHistory[month] = [historyItem];
        } else {
          newHistory[month].push(historyItem);
        }
      }

      setHistoryMonth(newHistory);
    }).catch(error => {
      console.log(error);
    });
  }, []);

  return (
    <div className={'history-page'}>
      <TitleBar title={'Lịch sử'}/>
      <div className={'history-list'}>
        {Object.keys(historyMonth).map(x => <HistoryGroup month={x} key={x} items={historyMonth[x]}/>)}
      </div>
    </div>
  );
}
