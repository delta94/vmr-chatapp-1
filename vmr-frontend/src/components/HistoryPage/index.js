import React, {useEffect, useState} from 'react';

import TitleBar from '../TitleBar';
import './HistoryPage.css';
import HistoryGroup from "../HistoryGroup";
import {getHistoryWithOffset} from "../../service/wallet";
import {timestampSecond2Month} from "../../util/string-util";
import {Empty} from "antd";

export default function HistoryPage() {
  let [historyMonth, setHistoryMonth] = useState({});
  let [offset, setOffset] = useState(0);

  let isEmpty = Object.keys(historyMonth).length === 0;

  function getHistory(onComplete) {
    getHistoryWithOffset(offset).then(result => {
      let newHistory = {...historyMonth};

      setOffset(offset + result.length);

      for (let item of result.reverse()) {
        let month = timestampSecond2Month(item.getTimestamp());

        let historyItem = {
          id: item.getId(),
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
      if (onComplete) {
        onComplete();
      }
    }).catch(error => {
      console.log(error);
    });
  }

  useEffect(() => {
    getHistory();
    // eslint-disable-next-line
  }, []);

  let handleScroll = (event) => {
    let el = event.target;
    let oldScrollTop = el.scrollTop;
    if (el.scrollHeight === oldScrollTop + el.clientHeight) {
      getHistory(() => {
        el.scrollTo(0, oldScrollTop);
      });
    }
  };

  return (
    <div className={'history-page'} onScroll={handleScroll}>
      <TitleBar title={'Lịch sử'}/>
      {!isEmpty &&
      <div className={'history-list'}>
        {Object.keys(historyMonth).map(x => <HistoryGroup month={x} key={x} items={historyMonth[x]}/>)}
      </div>}
      {isEmpty &&
      <div className={'history-list'}>
        <Empty description={<span>Bạn chưa thực hiện giao dịch nào</span>}/>
      </div>}
    </div>
  );
}
