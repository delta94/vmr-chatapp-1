import React from 'react';
import './HistoryItem.css';
import {Col, Row} from "antd";
import UserAvatar from "../UserAvatar";
import {moneyFormat, timestampSecond2String} from "../../util/string-util";

export default function HistoryItem(props) {
  let {type, friendName, amount, timestamp, message} = props;
  let msg = "Chuyển tiền tới";
  let sign = "-";
  if (type === 'receive') {
    msg = "Nhận tiền từ";
    sign = "+";
  }
  return (
    <div className={'history-item'}>
      <Row>
        <Col span={4} className={'icon-col'}>
          <UserAvatar name={"Dang Anh Van"} size={40}/>
        </Col>
        <Col span={20}>
          <Row>
            <Col xs={24} md={18}>
              <p className={'content'}>{msg} {friendName}</p>
              <p className={'time'}>{timestampSecond2String(timestamp)}</p>
              <p className={'message'}>{message}</p>
            </Col>
            <Col xs={24} md={6} className={'money-col'}>
              <span className={'amount'}>{sign} {moneyFormat(amount)} VNĐ</span>
            </Col>
          </Row>
        </Col>
      </Row>
    </div>
  )
};
