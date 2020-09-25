import React from 'react';
import './Toolbar.css';

export default function Toolbar(props) {
  const {title, leftItems, rightItems} = props;
  let className = "toolbar";
  if (props.className) {
    className += ` ${props.className}`;
  }
  return (
    <div className={className}>
      <div className="left-items">{leftItems}</div>
      <h1 className="toolbar-title">{title}</h1>
      <div className="right-items">{rightItems}</div>
    </div>
  );
}