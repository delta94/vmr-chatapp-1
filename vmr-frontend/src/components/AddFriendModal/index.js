import React from 'react';
import {Modal, Typography, Input, Row, Col} from 'antd';
import {SearchOutlined, UnorderedListOutlined} from '@ant-design/icons';
import {useSelector, useDispatch} from "react-redux";
import './add-friend-modal.css';
import {setSearchUserModalActive} from "../../redux/vmr-action";

const {Title} = Typography;
const {Search} = Input;

let emptySearch = (
  <Row>
  </Row>
);

function AddFriendModal() {
  let modalActive = useSelector(state => state.ui.searchUserActive);
  let dispatch = useDispatch();

  let closeModal = () => {
    dispatch(setSearchUserModalActive(false));
  };

  return (
    <Modal
      visible={modalActive}
      onCancel={closeModal}
    >
      <Title level={2} className="vmr-modal-title">
        <SearchOutlined className="friend-search-icon"/>
        Tìm user
      </Title>
      <Search placeholder="Nhập username hoặc name"/>
      <div className="user-list-search">
        {emptySearch}
      </div>
    </Modal>
  );
}

export default AddFriendModal;
