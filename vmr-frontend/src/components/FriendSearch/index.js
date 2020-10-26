import React from 'react';
import {Button} from 'antd';
import './FriendSearch.css';
import {useDispatch} from "react-redux";
import {setSearchUserModalActive} from "../../redux/vmr-action";
import {PlusOutlined} from "@ant-design/icons";

export default function FriendSearch(props) {
  let dispatch = useDispatch();

  let handleClick = () => {
    dispatch(setSearchUserModalActive(true));
  };

  return (
    <div className="friend-search">
      <input
        type="search"
        className="friend-search-input"
        placeholder={'Lọc bạn bè'}
        onChange={props.onFilter}
      />
      {/*<div*/}
      {/*  className="friend-search-input"*/}
      {/*>*/}
      {/*  {props.message}*/}
      {/*</div>*/}
      <Button type="primary" className="friend-search-button" onClick={handleClick}>
        <PlusOutlined/>Thêm
      </Button>
    </div>
  );
}
