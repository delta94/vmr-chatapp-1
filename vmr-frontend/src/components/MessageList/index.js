import React, {useEffect, useRef} from 'react';
import Compose from '../Compose';
import Toolbar from '../Toolbar';
import ToolbarButton from '../ToolbarButton';
import {connect} from 'react-redux';
import {getMessageFromAPI, updateActiveConservationId} from '../../redux/vmr-action';
import {getMessageList} from '../../service/message-list';
import renderMessageNew from './message-render';

import './MessageList.css';

let MessageList = (props) => {
  // Check if userMap is loaded
  if (!props.showFlag) {
    return null;
  }

  // Get receiver
  let receiverId = Number(props.match.params.receiverId);
  let receiver = props.receiver;

  // Check receiver
  if (!receiver) {
    return null;
  }

  let messageList = props.chatMessages;

  // Get websocket service from redux
  let webSocket = props.webSocket;

  // Messages list
  let messages = messageList.map((x) => {
    return {
      id: x.timestamp,
      message: x.message,
      author: x.senderId,
      timestamp: x.timestamp * 1000,
      isMine: x.isMine
    };
  });

  // Use to scroll message
  let endOfMsgList = useRef(null);
  let msgList = useRef(null);

  // Load message
  useEffect(
    () => {
      props.updateConversationId(receiverId);
      if (messageList.length === 0) {
        getMessageList(receiverId, messageList.length).then((data) => {
          props.updateMessageList(data, receiverId);
          endOfMsgList.current.scrollIntoView({behavior: 'smooth'});
        });
      }
    },
    [receiverId]
  );

  // Scroll to bottom of message list
  useEffect(
    () => {
      endOfMsgList.current.scrollIntoView({behavior: 'smooth'});
    },
    [props.scrollFlag, props.currentConversationId]
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

  let onChangeText = (event) => {
    if (event.keyCode === 13 && event.target.value !== '') {
      webSocket.send(receiverId, event.target.value);
      event.target.value = '';
    }
  };

  return (
    <div className="message-list">
      <Toolbar
        title={receiver.name}
        rightItems={[
          <ToolbarButton key="info" icon="ion-ios-information-circle-outline"/>,
          <ToolbarButton key="video" icon="ion-ios-videocam"/>,
          <ToolbarButton key="phone" icon="ion-ios-call"/>
        ]}
      />

      <div className="message-list-container" ref={msgList} onScroll={msgScrollHandle}>
        {renderMessageNew(messages)}
        <div ref={endOfMsgList} style={{height: '0px'}}/>
      </div>

      <Compose
        rightItems={[
          <ToolbarButton key="photo" icon="ion-ios-camera"/>,
          <ToolbarButton key="audio" icon="ion-ios-mic"/>,
          <ToolbarButton key="emoji" icon="ion-ios-happy"/>
        ]}
        onKeyUp={onChangeText}
      />
    </div>
  );
};

// Map from redux to props
let mapStateToPropsMessageList = (state, ownProp) => {
  let receiverId = Number(ownProp.match.params.receiverId);

  try {
    return {
      chatMessages: state.chat.chatMessagesHolder.chatMessages.get(receiverId),
      receiver: state.users.userMapHolder.userMap.get(receiverId),
      webSocket: state.app.webSocket,
      scrollFlag: state.chat.scrollFlag,
      currentConversationId: state.users.currentConversationId,
      showFlag: true
    };
  } catch (e) {
    return {
      showFlag: false
    }
  }
};

let mapDispatchToPropsMessageList = (dispatch) => {
  return {
    updateMessageList: (data, friendId) => {
      dispatch(getMessageFromAPI(data, friendId));
    },
    updateConversationId: (id) => {
      dispatch(updateActiveConservationId(id));
    }
  };
};

export default connect(mapStateToPropsMessageList, mapDispatchToPropsMessageList)(MessageList);
