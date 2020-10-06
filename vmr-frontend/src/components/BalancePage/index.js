import React from 'react';

import './BalancePage.css';
import {Col, Row} from "antd";
import {moneyFormat} from "../../util/string-util";
import {CalendarTwoTone, SmileTwoTone, UserOutlined} from '@ant-design/icons';
import {useBalance} from "../../hooks/wallet";
import TitleBar from '../TitleBar';

export default function BalancePage() {
  let balance = useBalance();

  return (
    <div className={'balance-page'}>
      <TitleBar title={'Số dư'}/>
      <Row className="balance-row">
        <Col xs={24} className="balance-col left">
          <div className="balance-container">
            <p>Số dư</p>
            <p><span className={"balance-number"}>{moneyFormat(balance)}</span> VNĐ</p>
          </div>
        </Col>

        <Col xs={24}>
          <Row justify="center" className="info-row">
            <Col span={3}><UserOutlined style={{color: 'red', fontSize: '30px'}}/></Col>
            <Col span={9} className="key">Tên tài khoản:</Col>
            <Col span={12} className="value">anhvan1999 </Col>
          </Row>

          <Row justify="center" className="info-row">
            <Col span={3}><SmileTwoTone twoToneColor="#52c41a" style={{fontSize: '30px'}}/></Col>
            <Col xs={9} className="key">Họ & Tên:</Col>
            <Col xs={12} className="value">Đặng Anh Văn</Col>
          </Row>

          <Row justify="center" className="info-row">
            <Col span={3}><CalendarTwoTone twoToneColor="#eb2f96" style={{fontSize: '30px'}}/></Col>
            <Col xs={9} className="key">Ngày cập nhật:</Col>
            <Col xs={12} className="value">01/10/2020</Col>
          </Row>
        </Col>

      </Row>
    </div>
  );
}
