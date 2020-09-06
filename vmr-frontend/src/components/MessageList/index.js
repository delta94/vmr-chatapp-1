import React, {useEffect, useRef} from 'react';
import Compose from '../Compose';
import Toolbar from '../Toolbar';
import ToolbarButton from '../ToolbarButton';
import Message from '../Message';
import {Switch, Route} from 'react-router-dom';
import moment from 'moment';
import {connect} from 'react-redux';
import {getMessageFromAPI, updateActiveConservationId} from '../../redux/vmr-action';
import {getMessageList} from "../../service/message-list";

import './MessageList.css';

let MessageList = (props) => {
  // Get user id
  let userId = Number(localStorage.getItem("userId"));

  // Check if userMap is loaded
  if (props.userMapHolder.userMap.size === 0
    || props.chatMessagesHolder.chatMessages.size === 0
    || props.webSocket.send === null) {
    return <div/>;
  }

  // Get receiver
  let receiverId = Number(props.match.params.receiverId);
  let receiver = props.userMapHolder.userMap.get(receiverId);

  // Get message list from redux
  let messageList = props.chatMessagesHolder.chatMessages.get(receiverId);

  // Get websocket service from redux
  let webSocket = props.webSocket;

  // Messages list
  let messages = messageList.map(x => {
    return {
      id: x.timestamp,
      message: x.message,
      author: x.senderId,
      timestamp: x.timestamp * 1000,
      isMine: x.isMine
    };
  });

  // Display message
  const renderMessages = () => {
    let i = 0;
    let messageCount = messages.length;
    let tempMessages = [];

    while (i < messageCount) {
      let previous = messages[i - 1];
      let current = messages[i];
      let next = messages[i + 1];
      let isMine = current.author === userId;
      let currentMoment = moment(current.timestamp);
      let prevBySameAuthor = false;
      let nextBySameAuthor = false;
      let startsSequence = true;
      let endsSequence = true;
      let showTimestamp = true;

      if (previous) {
        let previousMoment = moment(previous.timestamp);
        let previousDuration = moment.duration(currentMoment.diff(previousMoment));
        prevBySameAuthor = previous.author === current.author;

        if (prevBySameAuthor && previousDuration.as('hours') < 1) {
          startsSequence = false;
        }

        if (previousDuration.as('hours') < 1) {
          showTimestamp = false;
        }
      }

      if (next) {
        let nextMoment = moment(next.timestamp);
        let nextDuration = moment.duration(nextMoment.diff(currentMoment));
        nextBySameAuthor = next.author === current.author;

        if (nextBySameAuthor && nextDuration.as('hours') < 1) {
          endsSequence = false;
        }
      }

      tempMessages.push(
        <Message
          key={i}
          isMine={isMine}
          startsSequence={startsSequence}
          endsSequence={endsSequence}
          showTimestamp={showTimestamp}
          data={current}
        />
      );
      i += 1;
    }

    return tempMessages;
  }

  // Use to scroll message
  let endOfMsgList = useRef(null);
  let msgList = useRef(null);

  // Load message
  useEffect(() => {
    props.updateConversationId(receiverId);
    if (messageList.length === 0) {
      getMessageList(receiverId, messageList.length).then(data => {
        props.updateMessageList(data, receiverId);
        endOfMsgList.current.scrollIntoView({behavior: 'smooth'});
      });
    }
  }, [receiverId]);

  // Scroll to bottom of message list
  useEffect(() => {
    endOfMsgList.current.scrollIntoView({behavior: 'smooth'});
  }, [props.scrollFlag, props.currentConversationId]);

  // Load more message
  let msgScrollHandle = (event) => {
    let msgList = event.target;
    let offset = msgList.scrollTop;
    if (offset === 0) {
      getMessageList(receiverId, messageList.length).then(data => {
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
  }

  return (
    <div className="message-list">
      <Toolbar
        title={receiver.name}
        rightItems={[
          <ToolbarButton key="info" icon="ion-ios-information-circle-outline"/>,
          <ToolbarButton key="video" icon="ion-ios-videocam"/>,
          <ToolbarButton key="phone" icon="ion-ios-call"/>,
        ]}
      />

      <div className="message-list-container" ref={msgList} onScroll={msgScrollHandle}>
        {renderMessages()}
        <div ref={endOfMsgList} style={{height: "0px"}}/>
      </div>

      <Compose rightItems={[
        <ToolbarButton key="photo" icon="ion-ios-camera"/>,
        <ToolbarButton key="audio" icon="ion-ios-mic"/>,
        <ToolbarButton key="emoji" icon="ion-ios-happy"/>
      ]} onKeyUp={onChangeText}/>
    </div>
  );
}

// Map from redux to props
let mapStateToPropsMessageList = (state) => {
  return {
    userMapHolder: state.users.userMapHolder,
    chatMessagesHolder: state.chat.chatMessagesHolder,
    webSocket: state.app.webSocket,
    scrollFlag: state.chat.scrollFlag,
    currentConversationId: state.users.currentConversationId
  }
}

let mapDispatchToPropsMessageList = (dispatch) => {
  return {
    updateMessageList: (data, friendId) => {
      dispatch(getMessageFromAPI(data, friendId))
    },
    updateConversationId: (id) => {
      dispatch(updateActiveConservationId(id));
    }
  }
};

MessageList = connect(mapStateToPropsMessageList, mapDispatchToPropsMessageList)(MessageList);

export default function MessageListWrapper() {
  return (
    <Switch>
      <Route path="/t/:receiverId" component={MessageList}/>
      <Route exact path="/">
        <div/>
      </Route>
    </Switch>
  );
}
