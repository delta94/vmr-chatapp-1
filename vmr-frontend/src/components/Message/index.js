import React from 'react';
import moment from 'moment';
import './Message.css';
import {Col, Row} from "antd";
import {CheckCircleFilled, DollarCircleFilled} from '@ant-design/icons';

export default function Message(props) {
  const {
    data,
    isMine,
    startsSequence,
    endsSequence,
    showTimestamp,
    transfer,
    amount, message
  } = props;

  const friendlyTimestamp = moment(data.timestamp).format('LLLL');

  return (
    <div className={[
      'message',
      `${isMine ? 'mine' : ''}`,
      `${startsSequence ? 'start' : ''}`,
      `${endsSequence ? 'end' : ''}`
    ].join(' ')}>

      {
        showTimestamp &&
        <div className="timestamp">
          {friendlyTimestamp}
        </div>
      }

      <div className="bubble-container">
        {
          !transfer &&
          <div className="bubble" title={friendlyTimestamp}>
            {data.message}
          </div>
        }
        {
          transfer && isMine &&
          <div className="bubble transfer" title={friendlyTimestamp}>
            <Row gutter={[8, 8]}>
              <Col span={4}><CheckCircleFilled style={{fontSize: '30px'}}/></Col>
              <Col span={20}>Chuyển <span className={'amount'}>100 000</span> VNĐ
                <br/><span className={'msg'}>Test tính năng chuyển tiền</span>
              </Col>
            </Row>
          </div>
        }
        {
          transfer && !isMine &&
          <div className="bubble receive" title={friendlyTimestamp}>
            <Row gutter={[8, 8]}>
              <Col span={4}><DollarCircleFilled style={{fontSize: '30px'}}/></Col>
              <Col span={20}>Chuyển <span className={'amount'}>100 000</span> VNĐ cho bạn
                <br/><span className={'msg'}>Test tính năng chuyển tiền</span>
              </Col>
            </Row>
          </div>
        }
      </div>
    </div>
  );
}
