import React from 'react';
import './menu-bar.css';
import {useDispatch, useSelector} from "react-redux";
import {setTab} from "../../redux/vmr-action";
import {logout} from "../../service/user";

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

  return (
    <div className={className}>
      <IconWrapper icon={<MessageOutlined className="menubar-icon" style={{color: "green"}}/>}
                   onClick={() => setCurrentTab("chat")}
                   tab="chat"/>
      <IconWrapper icon={<CreditCardOutlined className="menubar-icon" style={{color: "#cc4d53"}}/>}
                   onClick={() => setCurrentTab("home")}
                   isMid
                   tab="home"/>
      <IconWrapper icon={<TeamOutlined className="menubar-icon" style={{color: "#7474ed"}}/>}
                   onClick={() => setCurrentTab("friend")}
                   isMid
                   tab={"friend"}/>
      <IconWrapper icon={<LogoutOutlined className="menubar-icon" style={{color: "purple"}}/>}
                   onClick={logout}/>
    </div>
  );
}

function IconWrapper(props) {
  let currentTab = useSelector(state => state.ui.currentTab);

  let {icon, onClick, tab, isMid} = props;

  let className = "icon-wrapper ";
  if (isMid) {
    className += " mid";
  }
  if (currentTab === tab) {
    className += " icon-wrapper-active";
  }

  return (
    <div className={className}
         onClick={onClick}>{icon}</div>
  );
}
