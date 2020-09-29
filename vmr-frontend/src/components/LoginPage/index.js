import React, {useState} from 'react';
import {Form, Input, Button, Row, Col, Card, Alert, Checkbox} from "antd";
import {UserOutlined, LockOutlined, LoginOutlined} from '@ant-design/icons';
import bg from '../resource/registerbg.jpg';
import {login} from "../../service/user";
import {Link} from 'react-router-dom';

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

function LoginPage(props) {
  let [form] = Form.useForm();
  let [error, setError] = useState(false);
  let [errorMessage, setErrorMessage] = useState("Credential not valid");

  let handleLogin = (event) => {
    login(event.username, event.password).then(() => {
      props.history.push('/');
    }).catch(msg => {
      form.resetFields();
      setError(true);
      setErrorMessage(msg);
    });
  };

  let cleanMsg = () => {
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

  return (
    <Row style={rowStyle} align="top">
      <Col xs={{span: 22, offset: 1}} sm={{span: 16, offset: 4}} md={{span: 10, offset: 7}} lg={{span: 8, offset: 8}}
           style={colStyle}>
        <h1 style={{textAlign: "center"}}>Login</h1>
        <Card bordered={false}>
          <Form form={form}
                onFinish={handleLogin}
                className="login-form"
                name="basic"
                initialValues={{remember: true}}
                onFieldsChange={cleanMsg}
          >
            <Form.Item
              name="username"
              rules={[{required: true, message: 'Please input your Username!'}]}
            >
              <Input prefix={<UserOutlined className="site-form-item-icon"/>} placeholder="Username"/>
            </Form.Item>
            <Form.Item
              name="password"
              rules={[{required: true, message: 'Please input your Password!'}]}
            >
              <Input
                prefix={<LockOutlined className="site-form-item-icon"/>}
                type="password"
                placeholder="Password"
              />
            </Form.Item>
            <Form.Item>
              <Form.Item name="remember" valuePropName="checked" noStyle>
                <Checkbox>Remember me</Checkbox>
              </Form.Item>

              <Link style={{float: "right"}} to="/login">
                Forgot password
              </Link>
            </Form.Item>

            <Form.Item>
              <Button type="primary" htmlType="submit" style={{width: "100%"}}>
                <LoginOutlined/>Log in
              </Button>
              Or <Link to="/register">register now!</Link>
            </Form.Item>
          </Form>
          {msg}
        </Card>
      </Col>
    </Row>
  );
}

export default LoginPage;
