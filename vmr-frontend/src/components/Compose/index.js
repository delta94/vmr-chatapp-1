import React from 'react';
import './Compose.css';

export default function Compose(props) {
  let {disable} = props;
  if (disable) {
    return (
      <div className="compose disable">
        <span className={'nonfriend'}>Các bạn không phải là bạn bè!</span>
      </div>
    );
  }
  return (
    <div className="compose">
      <input
        type="text"
        className="compose-input"
        placeholder="Nhập tin nhắn"
        onKeyUp={props.onKeyUp}
        ref={props.inputRef}
      />
      {
        props.rightItems
      }
    </div>
  );
}