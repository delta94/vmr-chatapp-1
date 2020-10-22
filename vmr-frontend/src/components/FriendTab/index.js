import React, {useEffect, useState} from "react";
import {getFriendList, setFriendStatus} from "../../service/friend";
import ConversationSearch from "../FriendSearch";
import {Avatar, Button, List, Menu, Dropdown} from "antd";
import {CloseOutlined} from "@ant-design/icons";
import {getColor} from "../../util/ui-util";
import {getFirstLetter} from "../../util/string-util";
import {useDispatch, useSelector} from "react-redux";
import {friendReload, setSideBarActive} from "../../redux/vmr-action";

import "./friend-tab.css";
import {Link} from "react-router-dom";
import AddFriendModal from "../AddFriendModal";

const {useHistory} = require("react-router-dom");
const {FriendStatus} = require('../../proto/vmr/friend_pb');

export default function FriendTab() {
  let [friendList, setFriendList] = useState([]);

  let friendReloadFlag = useSelector(state => state.ui.friendReloadFlag);

  useEffect(() => {
    getFriendList().then(res => {
      let friendListRes = res.getFriendInfoList();
      setFriendList(friendListRes);
    }).catch(err => {
      console.log(err);
    });
  }, [friendReloadFlag]);

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

  let history = useHistory();
  let dispatch = useDispatch();

  let button;
  let description = "Bạn bè";

  let acceptFriendBtnHandle = (e) => {
    e.preventDefault();
    setFriendStatus(item.getId(), 'ACCEPT').then(res => {
      console.log(res);
      dispatch(friendReload());
    }).catch(err => {
      console.log(err);
    });
  };

  let rejectFriendHandle = (e) => {
    e.preventDefault();
    setFriendStatus(item.getId(), 'REJECT').then(res => {
      console.log(res);
      dispatch(friendReload());
    }).catch(err => {
      console.log(err);
    });
  };

  let chatHandle = (event) => {
    event.preventDefault();
    history.push('/t/' + item.getId());
    dispatch(setSideBarActive(false));
  };

  let removeFriend = (event) => {
    event.preventDefault();
    setFriendStatus(item.getId(), 'REMOVE').then(() => {
      dispatch(friendReload());
    }).catch(err => {
      console.log(err);
    })
  }

  if (item.getFriendStatus() === FriendStatus.WAITING) {
    button = (
      <Button
        className="friend-modal-button"
        onClick={rejectFriendHandle}>
        <CloseOutlined/>Hủy lời mời
      </Button>
    );
    description = "Bạn đã gửi lời mời";
  } else if (item.getFriendStatus() === FriendStatus.NOT_ANSWER) {
    let menu = (
      <Menu>
        <Menu.Item>
          <a href="/" onClick={acceptFriendBtnHandle}>
            Chấp nhận
          </a>
        </Menu.Item>
        <Menu.Item>
          <a href="/" onClick={rejectFriendHandle}>
            Từ chối
          </a>
        </Menu.Item>
      </Menu>
    );

    button = (
      <Dropdown overlay={menu} placement="bottomLeft">
        <Button className="friend-modal-button">Trả lời</Button>
      </Dropdown>
    );
    description = "Đã gửi cho bạn lời mời";
  } else {
    let menu = (
      <Menu>
        <Menu.Item>
          <a href="/" onClick={chatHandle}>
            Chat ngay
          </a>
        </Menu.Item>
        <Menu.Item>
          <a href="/" onClick={removeFriend}>
            Hủy kết bạn
          </a>
        </Menu.Item>
      </Menu>
    );

    button = (
      <Dropdown overlay={menu} placement="bottomLeft">
        <Button className="friend-modal-button">Tùy chọn</Button>
      </Dropdown>
    );
  }

  return (
    <List.Item key={item.getId()} className="friend-tab-item">
      <List.Item.Meta
        avatar={
          <Avatar style={{backgroundColor: getColor(item.getId())}} size={50}>
            {getFirstLetter(item.getName())}
          </Avatar>
        }
        title={<Link to={"/t/" + item.getId()}>{item.getName()}</Link>}
        description={description}
      />
      <div>{button}</div>
    </List.Item>
  );
}
