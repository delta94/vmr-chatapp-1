import React from 'react';
import './ToolbarButton.css';

export default function ToolbarButton(props) {
  const {icon, onClick, type, style, active} = props;
  let className = "toolbar-btn-wrapper";
  if (type === 'top-bar-btn') {
    className += ' topbar-btn-wrapper';
  }
  if (type === 'compose-btn') {
    className += ' compose-btn-wrapper';
  }
  if (active) {
    className += ' compose-btn-active';
  }
  return (
    <div className={className} onClick={onClick} style={style}>
      {icon}
    </div>
  );
}