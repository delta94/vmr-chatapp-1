import React from 'react';
import ToolbarButton from "../ToolbarButton";
import {ArrowLeftOutlined, MoreOutlined} from "@ant-design/icons";
import Toolbar from "../Toolbar";
import {useOpenSideBar} from "../../hooks/ui";

export default function (props) {
  let openSideBar = useOpenSideBar();
  let {title} = props;

  return (
    <Toolbar
      title={title}
      className="chat-title-bar"
      rightItems={[
        <ToolbarButton key="info" icon={<MoreOutlined/>} type="top-bar-btn"/>
      ]}
      leftItems={[
        <ToolbarButton key="info" icon={<ArrowLeftOutlined/>} onClick={openSideBar} type={"top-bar-btn"}/>
      ]}
    />
  );
}