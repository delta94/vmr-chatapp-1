import React from 'react';
import {Avatar} from "antd";
import {getFirstLetter} from "../../util/string-util";
import {getColor} from "../../util/ui-util";

export default function UserAvatar(props) {
  let {name, size} = props;

  let avatarStyle = {
    backgroundColor: getColor(name.length)
  };

  return (
    <Avatar style={avatarStyle} size={size}>{getFirstLetter(name)}</Avatar>
  );
}