import React from 'react';
import WalletTabItem from "../WalletTabItem";

import {WalletOutlined, HistoryOutlined} from '@ant-design/icons';

export default function WalletTab() {
  return (
    <div className="conversation-list-scroll">
      <WalletTabItem
        icon={<WalletOutlined style={{color: '#ce353d'}}/>}
        title="Số Dư"
        description="Xem số dư, thông tin tài khoản"
        tabName="balance"
        path="/w/balance"/>
      <WalletTabItem
        icon={<HistoryOutlined style={{color: '#1c79dd'}}/>}
        title="Lịch sử"
        description="Xem lịch sử giao dịch"
        tabName="history"
        path="/w/history"/>
    </div>
  );
}