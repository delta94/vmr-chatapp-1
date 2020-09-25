import React, {useState} from 'react';
import {Modal, Typography, Input, Avatar, List, Button} from 'antd';
import {PlusOutlined, SearchOutlined} from '@ant-design/icons';
import {useSelector, useDispatch} from "react-redux";
import './add-friend-modal.css';
import {setSearchUserModalActive} from "../../redux/vmr-action";
import {queryUser} from "../../service/query-user";
import {getFirstLetter} from "../../util/string-util";
import {getColor} from "../../util/ui-util";

const {Title} = Typography;
const {Search} = Input;

function AddFriendModal() {
  let modalActive = useSelector(state => state.ui.searchUserActive);
  let dispatch = useDispatch();
  let [userList, setUserList] = useState([]);

  let closeModal = () => {
    dispatch(setSearchUserModalActive(false));
  };

  let searchHandle = (event) => {
    let queryString = event.target.value;
    queryUser(queryString).then(userListResult => {
      setUserList(userListResult);
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
          renderItem={item => (
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
              <div><Button type="primary"><PlusOutlined/>Kết bạn</Button></div>
            </List.Item>
          )}
        >
        </List>
      </div>
    </Modal>
  );
}

export default AddFriendModal;
