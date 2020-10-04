import React from 'react';
import {Row, Col} from 'antd';

import './WalletTabItem.css';
import {useDispatch, useSelector} from 'react-redux';
import {setWalletTab} from "../../redux/vmr-action";

export default function WalletTabItem(props) {
  let {icon, title, description, tabName} = props;
  let dispatch = useDispatch();
  let currentWalletTab = useSelector(state => state.ui.currentWalletTab);

  let itemClassName = 'wallet-tab-item';
  if (currentWalletTab === tabName) {
    itemClassName += ' active';
  }

  let clickHandle = () => {
    dispatch(setWalletTab(tabName));
  };

  return (
    <Row className={itemClassName} onClick={clickHandle}>
      <Col className="content-container" span={20}>
        {icon}
        <Col className="text-content">
          <h1 className="conversation-title">{title}</h1>
          <p className="description">{description}</p>
        </Col>
      </Col>
    </Row>
  );
}