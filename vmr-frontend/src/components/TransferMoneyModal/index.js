import React, {useState} from 'react';
import {Col, Modal, Row, Typography, InputNumber, Input, Button, Steps} from "antd";
import {
  ArrowLeftOutlined,
  ArrowRightOutlined,
  CheckCircleOutlined,
  CloseOutlined,
  DollarCircleOutlined
} from "@ant-design/icons";

import "./TransferMoneyModal.css";
import {moneyFormat} from "../../util/string-util";

const {Title} = Typography;
const {TextArea, Password} = Input;
const {Step} = Steps;

export default function TransferMoneyModal(props) {
  let {active, setActive, receiverName} = props;

  let [step, setStep] = useState(0);

  let closeModal = () => {
    setActive(false);
  };

  let footerButton = [
    <Button key="back" onClick={closeModal}>
      <CloseOutlined/> Hủy
    </Button>,
    <Button key="submit" type="primary" onClick={() => setStep(1)}>
      Tiếp theo <ArrowRightOutlined/>
    </Button>,
  ];

  if (step === 1) {
    footerButton = [
      <Button key="back" onClick={() => setStep(0)}>
        <ArrowLeftOutlined/>Quay lại
      </Button>,
      <Button key="submit" type="primary" onClick={() => setStep(3)}>
        Chuyển tiền <CheckCircleOutlined/>
      </Button>,
    ]
  }

  return (
    <Modal
      visible={active}
      onCancel={closeModal}
      footer={footerButton}
    >
      <Title level={4} className="vmr-modal-title">
        <DollarCircleOutlined className="transfer-money-icon" style={{color: 'red'}}/>
        Chuyển tiền tới <span style={{color: 'green'}}>{receiverName}</span>
      </Title>

      <Steps current={step} style={{paddingTop: '10px'}}>
        <Step title="Nhập số tiền"/>
        <Step title="Xác thực"/>
      </Steps>

      {step === 0 &&
      <div className="transfer-step">
        <Row className="transfer-row">
          <Col span={12}>Số dư khả dụng:</Col>
          <Col span={12}>100 000 VNĐ</Col>
        </Row>
        <Row className="transfer-row">
          <Col span={12}>Nhập số tiền (VNĐ):</Col>
          <Col span={12}>
            <InputNumber
              className="left-input"
              formatter={value => moneyFormat(value)}
            />
          </Col>
        </Row>
        <Row className="transfer-row">
          <Col span={12}>Nhập tin nhắn:</Col>
          <Col span={12}>
            <TextArea className="left-input"/>
          </Col>
        </Row>
      </div>
      }

      {step === 1 &&
      <div className="transfer-step">
        <Row className="transfer-row">
          <Col span={12}>Số dư khả dụng:</Col>
          <Col span={12}>100 000 VNĐ</Col>
        </Row>
        <Row className="transfer-row">
          <Col span={12}>Số tiền chuyển:</Col>
          <Col span={12}>100 000 VNĐ</Col>
        </Row>
        <Row className="transfer-row">
          <Col span={12}>Tin nhắn:</Col>
          <Col span={12}>Set nội dung tin nhắn</Col>
        </Row>
        <Row className="transfer-row">
          <Col span={12}>Xác minh mật khẩu:</Col>
          <Col span={12}><Password/></Col>
        </Row>
      </div>
      }

      {step === 3 &&
        <div className="transfer-step">

        </div>
      }
    </Modal>
  );
}