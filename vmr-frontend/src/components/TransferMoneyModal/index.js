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
import {transfer} from "../../service/wallet";
import useWindowSize from "../../hooks/window";
import {useBalance} from "../../hooks/wallet";

const {Title} = Typography;
const {TextArea, Password} = Input;
const {Step} = Steps;

const layout = {
  labelCol: {span: 8},
  wrapperCol: {span: 16},
};

export default function TransferMoneyModal(props) {
  let {active, setActive, receiverName, receiverId} = props;
  let [step, setStep] = useState(2);
  let [amount, setAmount] = useState(0);
  let [message, setMessage] = useState('');
  let [password, setPassword] = useState('');
  let [form] = Form.useForm(null);
  let [form2] = Form.useForm(null);
  let [valid, setValid] = useState(false);
  let windowSize = useWindowSize();
  let balance = useBalance(step, active);

  useEffect(() => {
    form.resetFields();
    setStep(0);
    setAmount(0);
    setValid(false);
    setPassword('');
    setMessage('Chuyển tiền');
    // eslint-disable-next-line
  }, [active]);

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

  let handleFieldChange = (changedFields, allFields) => {
    if (allFields.amount < 1000 || allFields.amount > balance) {
      setValid(false);
    } else {
      setValid(true);
    }
  };

  let handlePasswordChange = (changedFields, allFields) => {
    setPassword(allFields.password);
  };

  let handleTransfer = () => {
    transfer(receiverId, amount, password, message, Math.round(Math.random() * 1000)).then(data => {
      console.log(data);
      setStep(2);
    });
  };

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
      <Button key="submit" type="primary" onClick={handleTransfer} disabled={password.length === 0}>
        Chuyển tiền
      </Button>,
    ]
  }

  return (
    <Modal
      destroyOnClose={true}
      visible={active}
      onCancel={closeModal}
      centered
      footer={footerButton}
      className="transfer-modal"
    >
      <Title level={4} className="vmr-modal-title">
        <DollarCircleOutlined className="transfer-money-icon" style={{color: 'red', fontSize: '34px'}}/>
        Chuyển tiền tới <span style={{color: 'green'}}>{receiverName}</span>
      </Title>


      {windowSize >= 768 &&
      <Steps current={step} style={{paddingTop: '10px'}} size="small">
        <Step title="Nhập số tiền"/>
        <Step title="Xác thực"/>
        <Step title="Hoàn tất"/>
      </Steps>
      }

      {step === 0 &&
      <div className="transfer-step">
        <Form {...layout} form={form} initialValues={{'message': 'Chuyển tiền'}} onValuesChange={handleFieldChange}>
          <Form.Item label={"Số dư khả dụng"}>
            {moneyFormat(balance)} VNĐ
          </Form.Item>
          <Form.Item label={"Nhập số tiền"} name="amount"
                     rules={[{required: true, message: 'Vui lòng nhập số tiền'}, {validator: checkAmount}]}>
            <InputNumber className="left-input" formatter={value => moneyFormat(value)}/>
          </Form.Item>
          <Form.Item name="message" label={"Nhập lời nhắn"}
                     rules={[{required: true, message: 'Vui lòng nhập lời nhắn'}]}>
            <TextArea className="left-input"/>
          </Form.Item>
        </Form>
      </div>
      }

      {step === 1 &&
      <div className="transfer-step">
        <Form {...layout} form={form2} onValuesChange={handlePasswordChange}>
          <Form.Item label={"Số dư khả dụng"}>
            {moneyFormat(balance)} VNĐ
          </Form.Item>
          <Form.Item label={"Số tiền chuyển"}>
            {moneyFormat(amount)} VNĐ
          </Form.Item>
          <Form.Item label={"Lời nhắn"}>
            {message}
          </Form.Item>
          <Form.Item name="password" label={"Xác thực mật khẩu"}>
            <Password/>
          </Form.Item>
        </Form>
      </div>
      }

      {step === 2 &&
      <div className="transfer-step">
        <Row>
          <Col className="status-container" xs={24} md={8}><CheckCircleOutlined
            style={{color: 'green', fontSize: '100px'}}/></Col>
          <Col xs={24} md={16}>
            <Row className="status-row">
              <Col span={12}>Trạng thái:</Col>
              <Col span={12}>Thành công</Col>
            </Row>
            <Row className="status-row">
              <Col span={12}>Số tiền trừ:</Col>
              <Col span={12}>- 100 000 VNĐ</Col>
            </Row>
            <Row className="status-row">
              <Col span={12}>Số dư còn lại:</Col>
              <Col span={12}>0 VNĐ</Col>
            </Row>
          </Col>
        </Row>
      </div>
      }
    </Modal>
  );
}