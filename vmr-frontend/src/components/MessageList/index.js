import React, {useEffect, useRef, useState} from 'react';
import Compose from '../Compose';
import Toolbar from '../Toolbar';
import ToolbarButton from '../ToolbarButton';
import {connect} from 'react-redux';
import {getMessageFromAPI, setSideBarActive, updateActiveConservationId} from '../../redux/vmr-action';
import {getMessageList} from '../../service/message-list';
import renderMessageNew from './message-render';

import './MessageList.css';

import {ArrowLeftOutlined, MoreOutlined, SendOutlined, DollarCircleOutlined} from '@ant-design/icons';
import TransferMoneyModal from "../TransferMoneyModal";

let MessageListInternal = props => {
  let {scrollFlag, currentConversationId, receiverId, receiver, webSocket, chatMessages} = props;

  // Use to scroll message
  let endOfMsgList = useRef(null);
  let msgList = useRef(null);
  let inputRef = useRef(null);
  let [sendButtonActive, setSendBtnActive] = useState(false);
  let [moneyTransferActive, setMoneyTransferActive] = useState(true);

  // Message list
  let messages = chatMessages.map(x => {
    return {
      id: x.timestamp,
      message: x.message,
      author: x.senderId,
      timestamp: x.timestamp * 1000,
      isMine: x.isMine
    };
  });

  // Load message
  useEffect(
    () => {
      props.updateConversationId(receiverId);
      if (chatMessages.length === 0) {
        getMessageList(receiverId, chatMessages.length).then((data) => {
          props.updateMessageList(data, receiverId);
          let {current} = endOfMsgList;
          if (current) {
            current.scrollIntoView({behavior: 'smooth'});
          }
        });
      }
    },
    // eslint-disable-next-line
    [receiverId]
  );

  // Scroll to bottom of message list
  useEffect(
    () => {
      let {current} = endOfMsgList;
      if (current) {
        current.scrollIntoView({behavior: 'smooth'});
      }
    },
    [scrollFlag, currentConversationId]
  );

  // Load more message
  let msgScrollHandle = (event) => {
    let msgList = event.target;
    let offset = msgList.scrollTop;
    if (offset === 0) {
      getMessageList(receiverId, chatMessages.length).then((data) => {
        let oldHeight = msgList.scrollHeight;
        props.updateMessageList(data, receiverId);
        let newHeight = msgList.scrollHeight;
        msgList.scrollTo(0, newHeight - oldHeight);
      });
    }
  };

  // Send btn handle
  let handleSendButtonClick = () => {
    let {value} = inputRef.current;
    if (value !== '') {
      webSocket.send(receiverId, value);
    }
    inputRef.current.value = '';
    setSendBtnActive(false);
  };

  // Handle text change
  let onChangeText = (event) => {
    if (event.target.value !== '') {
      setSendBtnActive(true);
    } else {
      setSendBtnActive(false);
    }
    if (event.keyCode === 13 && event.target.value !== '') {
      handleSendButtonClick();
    }
  };

  let toggleSideBar = () => {
    props.openSideBar();
  };

  let openTransferModal = () => {
    setMoneyTransferActive(true);
  };

  return (
    <div className="message-list">
      <Toolbar
        title={receiver.name}
        className="chat-title-bar"
        rightItems={[
          <ToolbarButton key="info" icon={<MoreOutlined/>} type="top-bar-btn"/>
        ]}
        leftItems={[
          <ToolbarButton key="info" icon={<ArrowLeftOutlined/>} onClick={toggleSideBar} type={"top-bar-btn"}/>
        ]}
      />

      <div className="message-list-container" ref={msgList} onScroll={msgScrollHandle}>
        {renderMessageNew(messages)}
        <div ref={endOfMsgList} style={{height: '0px'}}/>
      </div>

      <Compose
        rightItems={[
          <ToolbarButton
            key="transfer"
            icon={<DollarCircleOutlined />}
            onClick={openTransferModal}
            type="compose-btn"
            style={{color:'red'}}
          />,
          <ToolbarButton
            key="send" icon={<SendOutlined/>}
            onClick={handleSendButtonClick}
            active={sendButtonActive}
            type="compose-btn"/>
        ]}
        onKeyUp={onChangeText}
        inputRef={inputRef}
      />

      <TransferMoneyModal
        active={moneyTransferActive}
        setActive={setMoneyTransferActive}
        receiverName={receiver.name}
      />
    </div>
  );
};

let MessageList = props => {
  // Check valid status of props
  if (!props.isValid()) {
    return null;
  }

  // Get receiver
  let receiverId = Number(props.match.params.receiverId);

  // Render internal component
  return <MessageListInternal {...props} receiverId={receiverId}/>
};

// Map from redux to props
let stateToProps = (state, ownProp) => {
  let receiverId = Number(ownProp.match.params.receiverId);

  try {
    return {
      chatMessages: state.chat.chatMessagesHolder.chatMessages.get(receiverId),
      receiver: state.users.userMapHolder.userMap.get(receiverId),
      webSocket: state.webSocket.webSocket,
      scrollFlag: state.chat.scrollFlag,
      currentConversationId: state.users.currentConversationId,
      isValid: function () {
        return this.chatMessages != null && this.receiver != null;
      }
    };
  } catch (e) {
    return {
      showFlag: false
    }
  }
};

let dispatchToProps = (dispatch) => {
  return {
    updateMessageList: (data, friendId) => {
      dispatch(getMessageFromAPI(data, friendId));
    },
    updateConversationId: (id) => {
      dispatch(updateActiveConservationId(id));
    },
    openSideBar: () => {
      dispatch(setSideBarActive(true));
    }
  };
};

export default connect(stateToProps, dispatchToProps)(MessageList);
