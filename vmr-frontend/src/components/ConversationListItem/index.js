import React, {useEffect} from 'react';
import {Avatar} from 'antd';
import shave from 'shave';

import './ConversationListItem.css';

const colorList = ['#f56a00', '#7265e6', '#ffbf00', '#00a2ae', '#007aff'];

function getFirstLetter(name) {
  let word = name.split(' ');
  return word[word.length - 1].charAt(0).toUpperCase();
}

export default function ConversationListItem(props) {

  let avatarStyle = {
    backgroundColor: colorList[Math.round(Math.random() * 10) % colorList.length]
  }

  useEffect(() => {
    shave('.conversation-snippet', 20);
  })

  const {name, text} = props.data;

  return (
    <div className="conversation-list-item">
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