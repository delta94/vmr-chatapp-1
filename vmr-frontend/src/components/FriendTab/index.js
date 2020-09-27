import React, {useEffect, useState} from "react";
import {getFriendList} from "../../service/friend";
import ConversationSearch from "../ConversationSearch";
import {Avatar, Button, List} from "antd";
import {CheckOutlined, CloseOutlined} from "@ant-design/icons";
import {getColor} from "../../util/ui-util";
import {getFirstLetter} from "../../util/string-util";
import "./friend-tab.css";

const {FriendStatus} = require('../../proto/vmr/friend_pb');

export default function FriendTab() {
  let [friendList, setFriendList] = useState([]);

  useEffect(() => {
    getFriendList().then(res => {
      let friendListRes = res.getFriendinfoList();
      setFriendList(friendListRes);
    }).catch(err => {
      console.log(err);
    });
  }, []);
  return (
    <div className="conversation-list-scroll">
      <ConversationSearch/>
      <List
        dataSource={friendList}
        renderItem={item => <FriendListItem item={item}/>}
      >
      </List>
    </div>
  );
}

function FriendListItem(props) {
  let {item} = props;
  console.log(item);

  let button;
  let description = "Bạn bè";

  if (item.getFriendStatus() === FriendStatus.WAITING) {
    button = <Button className="friend-modal-button"><CloseOutlined/>Hủy lời mời</Button>;
    description = "Đã gửi lời mời";
  } else if (item.getFriendStatus() === FriendStatus.NOT_ANSWER) {
    button = <Button className="friend-modal-button"><CheckOutlined/>Chấp nhận</Button>;
    description = "Đã gửi cho bạn lời mời";
  } else {
    button = <Button className="friend-modal-button"><CheckOutlined/>Chat ngay</Button>;
  }

  return (
    <List.Item key={item.getId()} className="friend-tab-item">
      <List.Item.Meta
        avatar={
          <Avatar style={{backgroundColor: getColor(item.getId())}} size={50}>
            {getFirstLetter(item.getName())}
          </Avatar>
        }
        title={<a href="https://ant.design">{item.getName()}</a>}
        description={description}
      />
      <div>{button}</div>
    </List.Item>
  );
}