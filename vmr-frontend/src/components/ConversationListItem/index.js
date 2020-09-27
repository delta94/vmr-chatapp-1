import React, {useEffect, useState} from 'react';
import {Avatar} from 'antd';
import shave from 'shave';
import {connect} from 'react-redux';
import './ConversationListItem.css';
import {setSideBarActive} from "../../redux/vmr-action";
import {getFirstLetter} from "../../util/string-util";
import {randColor} from "../../util/ui-util";

const {useHistory} = require('react-router-dom');

function ConversationListItem(props) {
  let history = useHistory();

  let [avatarStyle, setAvatarStyle] = useState({});

  let itemStyle = {
    borderRadius: "5px",
    marginLeft: "10px",
    marginRight: "10px"
  };

  useEffect(() => {
    shave('.conversation-snippet', 20);
    setAvatarStyle({
      backgroundColor: randColor()
    });
  }, []);

  const {name, text, id, isCurrentUser} = props.data;
  let online = props.userMap.get(id).online;

  let onlineStyle = "dot";
  if (isCurrentUser) {
    onlineStyle = "dot current-user"
  } else if (online) {
    onlineStyle = "dot online";
  }

  if (props.currentConversationId === id) {
    itemStyle.backgroundColor = 'rgba(0, 0, 0, .05)';
  }

  let clickHandle = () => {
    history.push('/t/' + id);
    props.hideSideBar();
  }

  return (
    <div className="conversation-list-item" onClick={clickHandle} style={itemStyle}>
      <Avatar style={avatarStyle} size={50}>
        {getFirstLetter(name)}
      </Avatar>
      <div className="conversation-info" style={{paddingLeft: "10px"}}>
        <h1 className="conversation-title"><span className={onlineStyle}/>{name}</h1>
        <p className="conservation-text" style={{marginBottom: 0, color: '#888'}}>{text}</p>
      </div>
    </div>
  );
}

function mapStateToProps(state) {
  return {
    currentConversationId: state.users.currentConversationId,
    userMap: state.users.userMapHolder.userMap,
    userMapHolder: state.users.userMapHolder
  }
}

function mapDispatchToProps(dispatch) {
  return {
    hideSideBar: () => {
      dispatch(setSideBarActive(false));
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(ConversationListItem);
