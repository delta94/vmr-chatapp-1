import React from 'react';
import {Row, Col} from 'antd';

import './WalletTabItem.css';
import {useDispatch, useSelector} from 'react-redux';
import {setWalletTab} from "../../redux/vmr-action";

const {useHistory} = require('react-router-dom');

export default function WalletTabItem(props) {
  let {icon, title, description, tabName, path} = props;
  let dispatch = useDispatch();
  let history = useHistory();
  let currentWalletTab = useSelector(state => state.ui.currentWalletTab);

  let itemClassName = 'wallet-tab-item';
  if (currentWalletTab === tabName) {
    itemClassName += ' active';
  }

  let clickHandle = () => {
    dispatch(setWalletTab(tabName));
    history.push(path);
  };

  return (
    <Row className={itemClassName} onClick={clickHandle}>
      <Col className="icon-content" span={6}>
        {icon}
      </Col>
      <Col className="text-content" span={18}>
        <h1 className="conversation-title">{title}</h1>
        <p className="description">{description}</p>
      </Col>
    </Row>
  );
}