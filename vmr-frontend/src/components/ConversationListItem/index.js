import React, {useEffect} from 'react';
import {Avatar} from 'antd';
import shave from 'shave';
import {useHistory} from 'react-router-dom';
import {connect} from 'react-redux';
import './ConversationListItem.css';

const colorList = ['#f56a00', '#7265e6', '#ffbf00', '#00a2ae', '#007aff'];

function getFirstLetter(name) {
  let word = name.split(' ');
  return word[word.length - 1].charAt(0).toUpperCase();
}

function ConversationListItem(props) {
  let history = useHistory();

  let avatarStyle = {
    backgroundColor: colorList[Math.round(Math.random() * 10) % colorList.length]
  }

  let itemStyle = {
    borderRadius: "10px",
    marginLeft: "10px",
    marginRight: "10px"
  };

  useEffect(() => {
    shave('.conversation-snippet', 20);
  })

  const {name, text, id} = props.data;

  if (props.currentConversationId === id) {
    itemStyle.backgroundColor = 'rgba(0, 0, 0, .05)';
  }

  let clickHandle = () => {
    history.push('/t/' + id);
  }

  return (
    <div className="conversation-list-item" onClick={clickHandle} style={itemStyle}>
      {/*<img className="conversation-photo" src={photo} alt="conversation" />*/}
      <Avatar style={avatarStyle} size={50}>
        {getFirstLetter(name)}
      </Avatar>
      <div className="conversation-info" style={{paddingLeft: "10px"}}>
        <h1 className="conversation-title">{name}</h1>
        <p className="conservation-text" style={{marginBottom: 0, color: '#888'}}>{text}</p>
      </div>
    </div>
  );
}

function mapStateToProps(state) {
  return {
    currentConversationId: state.currentConversationId
  }
}


export default connect(mapStateToProps, null)(ConversationListItem);