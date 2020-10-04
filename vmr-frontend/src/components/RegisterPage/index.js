import React, {useState} from 'react';
import {Form, Input, Button, Row, Col, Card, Alert} from "antd";
import {UserOutlined, LockOutlined, SmileOutlined, PlusCircleOutlined} from '@ant-design/icons';
import bg from '../resource/registerbg.jpg';
import "./Register.css";
import {register} from "../../service/user";

const {useHistory, Link} = require('react-router-dom');

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
  let [errorMessage, setErrorMessage] = useState('');

  let submitForm = (formData) => {
    console.log('Submit form data');
    let {username, fullname, password} = formData;
    register(username, fullname, password).then(() => {
      history.push('/');
    }).catch(error => {
      setErrorMessage(error.message);
      form.resetFields();
      setError(true);
    });
  };

  let clearMsg = () => {
    setError(false);
  };

  let msg = null;
  if (error) {
    msg = <Alert
      message={errorMessage}
      type="error"
      showIcon
    />;
  }

  let checkValidatePassword = (rule, value, callback) => {
    if (!value) {
      callback();
      return;
    }
    if (value.length < 8 && value.length > 0) {
      callback('Password not valid');
    } else if (value !== form.getFieldValue('password')) {
      callback('Password not match');
    } else {
      callback();
    }
  };

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
              rules={[
                {required: true, message: 'Please input your username!'},
                {min: 8, message: 'Username length must greater than or equal 8'}
              ]}
            >
              <Input prefix={<UserOutlined className="site-form-item-icon"/>} placeholder="Username"/>
            </Form.Item>

            <Form.Item
              name="fullname"
              rules={[{required: true, message: 'Please input your full name!'}]}
            >
              <Input prefix={<SmileOutlined className="site-form-item-icon"/>} placeholder="Full name"/>
            </Form.Item>

            <Form.Item
              name="password"
              type="password"
              rules={[{required: true, message: 'Please input your password!'}, {
                min: 8,
                message: 'Password length must greater than or equal to 8'
              }]}
            >
              <Input.Password prefix={<LockOutlined className="site-form-item-icon"/>} placeholder="Password"/>
            </Form.Item>

            <Form.Item
              name="vpassword"
              type="password"
              rules={[{required: true, message: 'Please validate your password!'},
                {validator: checkValidatePassword}]}
            >
              <Input.Password prefix={<LockOutlined className="site-form-item-icon"/>} placeholder="Validate password"/>
            </Form.Item>

            <Form.Item>
              <Button type="primary" htmlType="submit" style={{width: "100%"}}>
                <PlusCircleOutlined/>Register
              </Button>
              Or <Link to="/login">login now!</Link>
            </Form.Item>
          </Form>
          {msg}
        </Card>
      </Col>
    </Row>
  );
}

export default RegisterPage;
