import React, {useState} from 'react';
import {Form, Input, Button, Row, Col, Card, Alert} from "antd";
import {UserOutlined, LockOutlined, SmileOutlined, PlusCircleOutlined} from '@ant-design/icons';
import bg from '../resource/registerbg.jpg';
import "./Register.css";
import register from "../../service/register";
import {useHistory, Link} from 'react-router-dom';

const rowStyle = {
  minHeight: "100vh",
  backgroundImage: `url(${bg})`,
  backgroundSize: 'cover'
}

const colStyle = {
  border: '1px solid #e2dede',
  padding: '12px',
  borderRadius: '4px',
  marginTop: "10vh",
  marginBottom: "10vh",
  backgroundColor: "white"
};

function RegisterPage() {
  let history = useHistory();
  let [form] = Form.useForm();
  let [error, setError] = useState(false);

  let submitForm = (formData) => {
    register(formData.username, formData.fullname, formData.password).then(value => {
      history.push('/');
    }).catch(error => {
      form.resetFields();
      setError(true);
    });
  };

  let clearMsg = (event) => {
    setError(false);
  };

  let msg = null;
  if (error) {
    msg = <Alert
      message="Đăng ký không thành công"
      type="error"
      showIcon
    />;
  }

  return (
    <Row style={rowStyle} align="top">
      <Col xs={{span: 22, offset: 1}} sm={{span: 16, offset: 4}} md={{span: 12, offset: 6}} lg={{span: 10, offset: 7}}
           style={colStyle}>
        <h1 style={{textAlign: "center"}}>Register</h1>
        <Card bordered={false}>
          <Form
            name="basic"
            initialValues={{remember: true}}
            onFinish={submitForm}
            onFieldsChange={clearMsg}
            form={form}
          >

            <Form.Item
              name="username"
              rules={[{ required: true, message: 'Please input your username!' }]}
            >
              <Input prefix={<UserOutlined className="site-form-item-icon" />} placeholder="Username" />
            </Form.Item>

            <Form.Item
              name="fullname"
              rules={[{ required: true, message: 'Please input your full name!' }]}
            >
              <Input prefix={<SmileOutlined className="site-form-item-icon" />} placeholder="Full name" />
            </Form.Item>

            <Form.Item
              name="password"
              type="password"
              rules={[{ required: true, message: 'Please input your password!' }]}
            >
              <Input prefix={<LockOutlined className="site-form-item-icon" />} placeholder="Password" />
            </Form.Item>

            <Form.Item
              name="vpassword"
              type="password"
              rules={[{ required: true, message: 'Please validate your password!' }]}
            >
              <Input prefix={<LockOutlined className="site-form-item-icon" />} placeholder="Password" />
            </Form.Item>
        
            <Form.Item>
              <Button type="primary" htmlType="submit" style={{width: "100%"}}>
              <PlusCircleOutlined />Register
              </Button>
              Or <Link to="/login">login now!</Link>
            </Form.Item>
          </Form>
          <Row>
            <Col>
              {msg}
            </Col>
          </Row>
        </Card>
      </Col>
    </Row>
  );
}

export default RegisterPage;
