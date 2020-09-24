import React, {useEffect, useState} from 'react';
import {Avatar} from 'antd';
import shave from 'shave';
import {useHistory} from 'react-router-dom';
import {connect} from 'react-redux';
import './ConversationListItem.css';
import {setSideBarActive} from "../../redux/vmr-action";

const colorList = ['#f56a00', '#7265e6', '#ffbf00', '#00a2ae', '#007aff'];

function getFirstLetter(name) {
  let word = name.split(' ');
  return word[word.length - 1].charAt(0).toUpperCase();
}

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
      backgroundColor: colorList[Math.round(Math.random() * 10) % colorList.length]
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
      {/*<img className="conversation-photo" src={photo} alt="conversation" />*/}
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
