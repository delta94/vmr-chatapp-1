import React from 'react';
import './HistoryItem.css';
import {Col, Row} from "antd";
import UserAvatar from "../UserAvatar";

export default function HistoryItem(props) {
  return (
    <div className={'history-item'}>
      <Row>
        <Col span={4} className={'icon-col'}>
          <UserAvatar name={"Dang Anh Van"} size={40}/>
        </Col>
        <Col span={20}>
          <Row>
            <Col xs={24} md={18}>
              <p className={'content'}>Chuyển tiền tới Nguyễn Văn A</p>
              <p className={'time'}>13:58 - 01/10/2020</p>
              <p className={'message'}>Nội dung message</p>
            </Col>
            <Col xs={24} md={6} className={'money-col'}>
              <span className={'amount'}>+ 100 000 VNĐ</span>
            </Col>
          </Row>
        </Col>
      </Row>
    </div>
  )
};
