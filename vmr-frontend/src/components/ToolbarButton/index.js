import React from 'react';
import './ToolbarButton.css';

export default function ToolbarButton(props) {
  const { icon } = props;
  let className = `toolbar-button ${icon}`;
  if (props.active) {
    className += ' toolbar-button-active';
  }
  return (
    <span className={className} onClick={props.onClick} />
  );
}