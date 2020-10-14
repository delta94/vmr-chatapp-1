import React from 'react';
import ToolbarButton from "../ToolbarButton";
import {CaretLeftOutlined, MoreOutlined} from "@ant-design/icons";
import Toolbar from "../Toolbar";
import {useOpenSideBar} from "../../hooks/ui";
import useWindowSize from "../../hooks/window";

export default function TitleBar(props) {
  let openSideBar = useOpenSideBar();
  let {title} = props;
  let windowWidth = useWindowSize();

  let leftItems = [];
  if (windowWidth < 700) {
    leftItems = [
      <ToolbarButton
        key="info"
        icon={<CaretLeftOutlined style={{color: '#1890ff'}}/>}
        onClick={openSideBar}
        type={"top-bar-btn"}/>
    ];
  }

  return (
    <Toolbar
      title={title}
      className="chat-title-bar"
      rightItems={[
        <ToolbarButton key="info" icon={<MoreOutlined/>} type="top-bar-btn"/>
      ]}
      leftItems={leftItems}
    />
  );
}
