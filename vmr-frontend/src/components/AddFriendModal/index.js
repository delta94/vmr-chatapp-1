import React, {useState} from 'react';
import {Modal, Typography, Input, Avatar, List, Button} from 'antd';
import {CheckOutlined, PlusOutlined, SearchOutlined} from '@ant-design/icons';
import {useSelector, useDispatch} from "react-redux";
import './add-friend-modal.css';
import {friendReload, setSearchUserModalActive} from "../../redux/vmr-action";
import {getFirstLetter} from "../../util/string-util";
import {getColor} from "../../util/ui-util";
import {acceptFriend, addFriend, queryUser} from "../../service/friend";
import {getUserId} from "../../util/auth-util";

const {useHistory} = require('react-router-dom');
const {FriendStatus} = require("../../proto/vmr/friend_pb");
const {Title, Text} = Typography;
const {Search} = Input;

function AddFriendModal() {
  let modalActive = useSelector(state => state.ui.searchUserActive);
  let dispatch = useDispatch();
  let [userList, setUserList] = useState([]);
  let userId = getUserId();

  let closeModal = () => {
    dispatch(setSearchUserModalActive(false));
  };

  let searchHandle = (event) => {
    let queryString = event.target.value;
    queryUser(queryString).then(userListResult => {
      setUserList(userListResult.filter(x => x.getId() !== userId));
    });
  };

  return (
    <Modal
      visible={modalActive}
      onCancel={closeModal}
      footer={[
        <Button key="back" onClick={closeModal}>
          Đóng
        </Button>
      ]}
    >
      <Title level={2} className="vmr-modal-title">
        <SearchOutlined className="friend-search-icon"/>
        Tìm user
      </Title>
      <Search placeholder="Nhập username hoặc name" onChange={searchHandle}/>
      <div className="user-list-search">
        <List
          dataSource={userList}
          renderItem={item => <SearchListItem item={item}/>}
        >
        </List>
      </div>
    </Modal>
  );
}

function SearchListItem(props) {
  let {item} = props;
  let dispatch = useDispatch();
  let history = useHistory();

  let [addFriendSucceeded, setAddFriendSucceeded] = useState(false);
  let [acceptSucceeded, setAcceptSucceeded] = useState(false);

  let handleAddFriend = () => {
    addFriend(item.getId()).then(r => {
      console.log(r);
      setAddFriendSucceeded(true);
      dispatch(friendReload());
    }).catch(err => {
      console.log(err);
    });
  };

  let acceptFriendBtnHandle = () => {
    acceptFriend(item.getId()).then(res => {
      console.log(res);
      setAcceptSucceeded(true);
      dispatch(friendReload());
    }).catch(err => {
      console.log(err);
    });
  };

  let chatHandle = () => {
    dispatch(setSearchUserModalActive(false));
    history.push('/t/' + item.getId());
  }

  let button = <Button type="primary" className="friend-modal-button" onClick={handleAddFriend}><PlusOutlined/>Kết
    bạn</Button>;

  if (acceptSucceeded || item.getFriendstatus() === FriendStatus.FRIEND) {
    button = <Button className="friend-modal-button" onClick={chatHandle}>Chat ngay</Button>;
  } else if (addFriendSucceeded || item.getFriendstatus() === FriendStatus.WAITING) {
    button = <Text type="secondary">Đang chờ phản hồi</Text>;
  } else if (item.getFriendstatus() === FriendStatus.NOT_ANSWER) {
    button = <Button className="friend-modal-button" onClick={acceptFriendBtnHandle}><CheckOutlined/>Chấp
      nhận</Button>;
  }

  return (
    <List.Item key={item.getId()}>
      <List.Item.Meta
        avatar={
          <Avatar style={{backgroundColor: getColor(item.getId())}} size={50}>
            {getFirstLetter(item.getName())}
          </Avatar>
        }
        title={<a href="https://ant.design">{item.getName()}</a>}
        description={"@" + item.getUsername()}
      />
      <div>{button}</div>
    </List.Item>
  );
}

export default AddFriendModal;
