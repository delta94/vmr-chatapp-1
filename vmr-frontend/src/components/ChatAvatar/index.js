import React from 'react';
import {getFirstLetter} from "../../util/string-util";
import {getColor} from "../../util/ui-util";
import {Avatar} from "antd";
import "./Avatar.css";

export default function ChatAvatar(props) {
  let {name, onlineStatus} = props;

  let avatarStyle = {
    backgroundColor: getColor(name.length)
  };

  return (
    <div className="vmr-avatar">
      <Avatar style={avatarStyle} size={50}>
        {getFirstLetter(name)}
      </Avatar>
      {onlineStatus && <span className={"badge "}/>}
    </div>
  )
}
