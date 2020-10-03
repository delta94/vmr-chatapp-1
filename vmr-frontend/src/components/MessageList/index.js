import React, {useEffect, useRef, useState} from 'react';
import Compose from '../Compose';
import Toolbar from '../Toolbar';
import ToolbarButton from '../ToolbarButton';
import {connect} from 'react-redux';
import {getMessageFromAPI, setSideBarActive, updateActiveConservationId} from '../../redux/vmr-action';
import {getMessageList} from '../../service/message-list';
import renderMessageNew from './message-render';

import './MessageList.css';

import {ArrowLeftOutlined, MoreOutlined, SendOutlined} from '@ant-design/icons';

let MessageListInternal = props => {
  let {scrollFlag, currentConversationId, receiverId, receiver, webSocket} = props;

  // Use to scroll message
  let endOfMsgList = useRef(null);
  let msgList = useRef(null);

  let messageList = props.chatMessages;
  let messages = messageList.map(x => {
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
      if (messageList.length === 0) {
        getMessageList(receiverId, messageList.length).then((data) => {
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
      getMessageList(receiverId, messageList.length).then((data) => {
        let oldHeight = msgList.scrollHeight;
        props.updateMessageList(data, receiverId);
        let newHeight = msgList.scrollHeight;
        msgList.scrollTo(0, newHeight - oldHeight);
      });
    }
  };

  let inputRef = useRef(null);
  let [sendButtonActive, setSendBtnActive] = useState(false);

  let handleSendButtonClick = () => {
    let {value} = inputRef.current;
    if (value !== '') {
      webSocket.send(receiverId, value);
    }
    inputRef.current.value = '';
    setSendBtnActive(false);
  };

  let onChangeText = (event) => {
    if (event.target.value !== '') {
      setSendBtnActive(true);
    }
    if (event.keyCode === 13 && event.target.value !== '') {
      handleSendButtonClick();
    }
  };

  let toggleSideBar = () => {
    props.openSideBar();
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
          <ToolbarButton key="emoji" icon={<SendOutlined/>}
                         onClick={handleSendButtonClick}
                         active={sendButtonActive}
                         type="compose-btn"/>
        ]}
        onKeyUp={onChangeText}
        inputRef={inputRef}
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
