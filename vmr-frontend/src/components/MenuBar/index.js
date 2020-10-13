import React from 'react';
import './menu-bar.css';
import {useDispatch, useSelector} from "react-redux";
import {setTab} from "../../redux/vmr-action";
import {logout} from "../../service/user";
import {Badge} from "antd";
import {CreditCardOutlined, MessageOutlined, TeamOutlined, LogoutOutlined} from '@ant-design/icons';

export default function MenuBar(props) {
  let dispatch = useDispatch();

  let setCurrentTab = (tab) => {
    dispatch(setTab(tab));
  };

  let className = "menubar";

  if (props.className) {
    className += ` ${props.className}`;
  }

  let numChatNotifications = useSelector(state => {
    return Object.values(state.friends.friends).map(x => x.numNotifications)
      .reduce((x, y) => x + y, 0);
  });

  return (
    <div className={className}>
      <IconWrapper icon={<MessageOutlined className="menubar-icon" style={{color: '#1890ff'}}/>}
                   onClick={() => setCurrentTab("chat")}
                   numMsg={numChatNotifications}
                   tab="chat"/>
      <IconWrapper icon={<CreditCardOutlined className="menubar-icon" style={{color: '#1890ff'}}/>}
                   onClick={() => setCurrentTab("wallet")}
                   isMid
                   tab="wallet"/>
      <IconWrapper icon={<TeamOutlined className="menubar-icon" style={{color: '#1890ff'}}/>}
                   onClick={() => setCurrentTab("friend")}
                   isMid
                   tab={"friend"}/>
      <IconWrapper icon={<LogoutOutlined className="menubar-icon" style={{color: '#1890ff'}}/>}
                   onClick={logout}/>
    </div>
  );
}

function IconWrapper(props) {
  let currentTab = useSelector(state => state.ui.currentTab);

  let {icon, onClick, tab, isMid, numMsg} = props;

  let className = "icon-wrapper ";
  if (isMid) {
    className += " mid";
  }
  if (currentTab === tab) {
    className += " icon-wrapper-active";
  }

  return (
    <div className={className} onClick={onClick}>
      <Badge size="small" count={numMsg} offset={[0, 15]}>
        {icon}
      </Badge>
    </div>
  );
}
