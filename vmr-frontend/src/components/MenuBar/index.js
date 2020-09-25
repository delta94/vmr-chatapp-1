import React from 'react';
import './menu-bar.css';

export default function MenuBar(props) {
  let className = "menubar";
  if (props.className) {
    className += ` ${props.className}`;
  }
  return (
    <div className={className}>
      {props.items.map(item => {
        if (item.props.isCurrent) {
          return <div className="menubar-item menubar-item-active" key={item.key}>{item}</div>;
        }
        return <div className="menubar-item" key={item.key}>{item}</div>;
      })}
    </div>
  );
}