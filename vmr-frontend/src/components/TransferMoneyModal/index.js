import React, {useState, useEffect} from 'react';
import {Col, Modal, Row, Typography, InputNumber, Input, Button, Steps, Form} from "antd";
import {
  ArrowLeftOutlined,
  ArrowRightOutlined,
  CheckCircleOutlined,
  CloseOutlined,
  DollarCircleOutlined
} from "@ant-design/icons";

import "./TransferMoneyModal.css";
import {moneyFormat} from "../../util/string-util";
import {getBalance} from "../../service/wallet";

const {Title} = Typography;
const {TextArea, Password} = Input;
const {Step} = Steps;

export default function TransferMoneyModal(props) {
  let {active, setActive, receiverName} = props;
  let [balance, setBalance] = useState();
  let [step, setStep] = useState(0);
  let [amount, setAmount] = useState(0);
  let [message, setMessage] = useState('');
  let [password, setPassword] = useState('');
  let [form] = Form.useForm();
  let [form2] = Form.useForm();
  let [valid, setValid] = useState(false);

  useEffect(() => {
    form.resetFields();
    setStep(0);
    setAmount(0);
    setValid(false);
    setMessage('Chuyển tiền');
  }, [active]);

  useEffect(() => {
    getBalance().then(result => {
      setBalance(result.getBalance());
    });
  }, [step, active]);

  let closeModal = () => {
    setActive(false);
  };

  let validateAndMoveNext = () => {
    setAmount(form.getFieldValue('amount'));
    setMessage(form.getFieldValue('message'));
    setStep(1);
  };

  let checkAmount = (rule, value, callback) => {
    if (value < 1000) {
      callback('Số tiền chuyển phải từ 1000đ trở lên');
    } else if (value > balance) {
      callback('Số tiền không được vượt quá balance')
    }
  };

  function handleFieldChange(changedFields, allFields) {
    if (allFields.amount < 1000 || allFields.amount > balance) {
      setValid(false);
    } else {
      setValid(true);
    }
  }

  function handlePasswordChange(changedFields, allFields) {
    setPassword(allFields.password);
  }

  let footerButton = [
    <Button key="back" onClick={closeModal}>
      <CloseOutlined/> Hủy
    </Button>,
    <Button key="submit" type="primary" onClick={validateAndMoveNext} disabled={!valid}>
      Tiếp theo <ArrowRightOutlined/>
    </Button>,
  ];

  if (step === 1) {
    footerButton = [
      <Button key="back" onClick={() => setStep(0)}>
        <ArrowLeftOutlined/>Quay lại
      </Button>,
      <Button key="submit" type="primary" onClick={() => setStep(3)} disabled={password.length === 0}>
        Chuyển tiền <CheckCircleOutlined/>
      </Button>,
    ]
  }

  return (
    <Modal
      destroyOnClose={true}
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
        <Form
          form={form}
          initialValues={{
            'message': 'Chuyển tiền'
          }}
          onValuesChange={handleFieldChange}>
          <Row className="transfer-row">
            <Col span={12}>Số dư khả dụng:</Col>
            <Col span={12}>
              <Form.Item>
                {moneyFormat(balance)} VNĐ
              </Form.Item>
            </Col>
          </Row>
          <Row className="transfer-row">
            <Col span={12}>Nhập số tiền (VNĐ):</Col>
            <Col span={12}>
              <Form.Item
                name="amount"
                rules={[{required: true, message: 'Vui lòng nhập số tiền'}, {validator: checkAmount}]}>
                <InputNumber
                  className="left-input"
                  formatter={value => moneyFormat(value)}
                />
              </Form.Item>
            </Col>
          </Row>
          <Row className="transfer-row">
            <Col span={12}>Nhập tin nhắn:</Col>
            <Col span={12}>
              <Form.Item name="message">
                <TextArea className="left-input"/>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </div>
      }

      {step === 1 &&
      <div className="transfer-step">
        <Form form={form2} onValuesChange={handlePasswordChange}>
          <Row className="transfer-row">
            <Col span={12}>Số dư khả dụng:</Col>
            <Col span={12}>
              <Form.Item>
                <Col span={12}>100 000 VNĐ</Col>
              </Form.Item>
            </Col>
          </Row>
          <Row className="transfer-row">
            <Col span={12}>Nhập số tiền (VNĐ):</Col>
            <Col span={12}>
              <Form.Item>
                <Col>{amount} VNĐ</Col>
              </Form.Item>
            </Col>
          </Row>
          <Row className="transfer-row">
            <Col span={12}>Nhập tin nhắn:</Col>
            <Col span={12}>
              <Form.Item>
                {message}
              </Form.Item>
            </Col>
          </Row>
          <Row className="transfer-row">
            <Col span={12}>Xác thực mật khẩu:</Col>
            <Col span={12}>
              <Form.Item name="password">
                <Password/>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </div>
      }

      {step === 3 &&
      <div className="transfer-step">

      </div>
      }
    </Modal>
  );
}