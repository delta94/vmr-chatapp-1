import React from 'react';

import './BalancePage.css';
import {Col, Row} from "antd";
import {moneyFormat} from "../../util/string-util";
import {CalendarTwoTone, SmileTwoTone, UserOutlined} from '@ant-design/icons';
import {useBalance} from "../../hooks/wallet";

export default function BalancePage() {
  let balance = useBalance();

  return (
    <div className={'balance-page'}>
      <Row className="balance-row">
        <Col xs={24} className="balance-col left">
          <div className="balance-container">
            <p>Số dư</p>
            <p><span className={"balance-number"}>{moneyFormat(balance)}</span> VNĐ</p>
          </div>
        </Col>

        <Col xs={24}>
          <Row justify="center" className="info-row">
            <Col xs={12} className="key"><UserOutlined style={{color: 'red', fontSize: '30px'}}/> Tên tài khoản: </Col>
            <Col xs={12} className="value">anhvan1999 </Col>
          </Row>

          <Row justify="center" className="info-row">
            <Col xs={12} className="key"><SmileTwoTone twoToneColor="#52c41a" style={{fontSize: '30px'}}/> Họ & Tên:
            </Col>
            <Col xs={12} className="value">Đặng Anh Văn</Col>
          </Row>

          <Row justify="center" className="info-row">
            <Col xs={12} className="key"><CalendarTwoTone twoToneColor="#eb2f96" style={{fontSize: '30px'}}/> Ngày cập
              nhật gần nhất: </Col>
            <Col xs={12} className="value">01/10/2020</Col>
          </Row>
        </Col>

      </Row>
    </div>
  );
}
