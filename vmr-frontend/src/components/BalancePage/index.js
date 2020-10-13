import React from 'react';

import './BalancePage.css';
import {Col, Row} from "antd";
import {moneyFormat, timestampSecond2String} from "../../util/string-util";
import {CalendarTwoTone, SmileTwoTone, UserOutlined} from '@ant-design/icons';
import {useInfoWithBalance} from "../../hooks/wallet";
import TitleBar from '../TitleBar';

export default function BalancePage() {
  let info = useInfoWithBalance();

  return (
    <div className={'balance-page'}>
      <TitleBar title={'Số dư'}/>
      <Row className="balance-row">
        <Col xs={24} className="balance-col left">
          <div className="balance-container">
            <p>Số dư</p>
            <p><span className={"balance-number"}>{moneyFormat(info.balance)}</span> VNĐ</p>
          </div>
        </Col>

        <Col xs={24}>
          <Row justify="center" className="info-row">
            <Col span={3}><UserOutlined style={{color: '#096dd9', fontSize: '30px'}}/></Col>
            <Col span={9} className="key">Tên tài khoản:</Col>
            <Col span={12} className="value">{info.userName} </Col>
          </Row>

          <Row justify="center" className="info-row">
            <Col span={3}><SmileTwoTone twoToneColor="#096dd9" style={{fontSize: '30px'}}/></Col>
            <Col xs={9} className="key">Họ & Tên:</Col>
            <Col xs={12} className="value">{info.name}</Col>
          </Row>

          <Row justify="center" className="info-row">
            <Col span={3}><CalendarTwoTone twoToneColor="#096dd9" style={{fontSize: '30px'}}/></Col>
            <Col xs={9} className="key">Ngày cập nhật:</Col>
            <Col xs={12} className="value">{timestampSecond2String(info.lastUpdated)}</Col>
          </Row>
        </Col>

      </Row>
    </div>
  );
}
