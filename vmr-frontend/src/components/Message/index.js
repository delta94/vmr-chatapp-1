import React from 'react';
import moment from 'moment';
import './Message.css';
import {Col, Row} from "antd";
import {CheckCircleFilled, DollarCircleFilled} from '@ant-design/icons';
import {moneyFormat} from "../../util/string-util";
import NewLineText from "../NewLineText";

export default function Message(props) {
  const {
    data,
    isMine,
    startsSequence,
    endsSequence,
    showTimestamp
  } = props;

  const {transfer, message} = data;
  let msgGroup = message.match(/(\d+);(.+)/);

  const friendlyTimestamp = moment(data.timestamp).format('HH:mm DD-MM-YYYY');

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
            {/*{data.message}*/}
            <NewLineText text={data.message}/>
          </div>
        }
        {
          transfer && isMine &&
          <div className="bubble transfer" title={friendlyTimestamp}>
            <Row gutter={[8, 8]}>
              <Col span={4}><CheckCircleFilled style={{fontSize: '30px'}}/></Col>
              <Col span={20}>
                <div>
                  Chuyển <span className={'amount'}>
                {moneyFormat(Number(msgGroup[1]))}</span> VNĐ
                </div>
                <div className={'msg'}>
                  <NewLineText text={msgGroup[2]}/>
                </div>
              </Col>
            </Row>
          </div>
        }
        {
          transfer && !isMine &&
          <div className="bubble receive" title={friendlyTimestamp}>
            <Row gutter={[8, 8]}>
              <Col span={4}><DollarCircleFilled style={{fontSize: '30px'}}/></Col>
              <Col span={20}>
                <div>
                  Chuyển cho bạn <span className={'amount'}>
                {moneyFormat(Number(msgGroup[1]))}</span> VNĐ
                </div>
                <div className={'msg'}>
                  <NewLineText text={msgGroup[2]}/>
                </div>
              </Col>
            </Row>
          </div>
        }
      </div>
    </div>
  );
}
