import React from 'react';
import {Button} from 'antd';
import './FriendSearch.css';
import {useDispatch, useSelector} from "react-redux";
import {setSearchUserModalActive} from "../../redux/vmr-action";
import {PlusOutlined} from "@ant-design/icons";

export default function FriendSearch() {
  let dispatch = useDispatch();

  let friendsNumber = useSelector(state => Object.keys(state.friends.friends).length);

  let handleClick = () => {
    dispatch(setSearchUserModalActive(true));
  };

  return (
    <div className="friend-search">
      <input
        type="search"
        className="friend-search-input"
        placeholder={"Bạn bè: " + friendsNumber}
        disabled
      />
      <Button type="primary" className="friend-search-button" onClick={handleClick}>
        <PlusOutlined/>Thêm
      </Button>
    </div>
  );
}
