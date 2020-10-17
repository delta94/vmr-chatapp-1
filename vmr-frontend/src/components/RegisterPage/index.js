import React, {useState} from 'react';
import {Form, Input, Button, Row, Col, Card, Alert} from "antd";
import {UserOutlined, LockOutlined, SmileOutlined, PlusCircleOutlined} from '@ant-design/icons';
import {register} from "../../service/user";
import responseCode from '../../const/responseCode';

import bg from '../resource/registerbg.jpg';

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
    let {username, fullname, password} = formData;

    register(username, fullname, password).then(() => {
      history.push('/');
    }).catch(errorData => {
      // Show error message
      switch (errorData['responseCode']) {
        case responseCode.USERNAME_EXISTED:
          setErrorMessage('Tên người dùng đã tồn tại');
          break;
        case responseCode.CREDENTIALS_NOTVALID:
          setErrorMessage('Username hoặc mật khẩu bạn nhập không hợp lệ');
          break;
        default:
          setErrorMessage('Không kết nối được với server, vui lòng thử lại');
      }

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

  let checkValidatePassword = async (rule, value) => {
    if (!value) {
      return;
    }

    if (value.length < 8 && value.length > 0) {
      throw new Error('Password not valid');
    } else if (value !== form.getFieldValue('password')) {
      throw new Error('Password not match');
    }
  };

  return (
    <Row style={rowStyle} align="top">
      <Col xs={{span: 22, offset: 1}}
           sm={{span: 16, offset: 4}}
           md={{span: 10, offset: 7}}
           lg={{span: 8, offset: 8}}
           xl={{span: 6, offset: 9}}
           style={colStyle}>
        <h1 style={{textAlign: "center"}}>Đăng ký</h1>
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
                {required: true, message: 'Vui lòng nhập tên tài khoản'},
                {min: 8, message: 'Tên tài khoản phải dài tối thiểu 8 ký tự'}
              ]}
            >
              <Input prefix={<UserOutlined className="site-form-item-icon"/>} placeholder="Tên tài khoản"/>
            </Form.Item>

            <Form.Item
              name="fullname"
              rules={[{required: true, message: 'Vui lòng nhập họ tên'}]}
            >
              <Input prefix={<SmileOutlined className="site-form-item-icon"/>} placeholder="Họ & tên"/>
            </Form.Item>

            <Form.Item
              name="password"
              type="password"
              rules={[{required: true, message: 'Vui lòng nhập mật khẩu'}, {
                min: 8,
                message: 'Mật khẩu phải dài tối thỉểu 8 ký tự'
              }]}
            >
              <Input.Password prefix={<LockOutlined className="site-form-item-icon"/>} placeholder="Mật khẩu"/>
            </Form.Item>

            <Form.Item
              name="vpassword"
              type="password"
              rules={[{required: true, message: 'Vui lòng xác minh mật khẩu của bạn'},
                {validator: checkValidatePassword}]}
            >
              <Input.Password prefix={<LockOutlined className="site-form-item-icon"/>} placeholder="Nhập lại mật khẩu"/>
            </Form.Item>

            <Form.Item>
              <Button type="primary" htmlType="submit" style={{width: "100%"}}>
                <PlusCircleOutlined/>Đăng ký
              </Button>
              <p style={{paddingTop: '10px'}}>Đã có tài khoản? <Link to="/login">đăng nhập ngay</Link></p>
            </Form.Item>
          </Form>
          {msg}
        </Card>
      </Col>
    </Row>
  );
}

export default RegisterPage;
