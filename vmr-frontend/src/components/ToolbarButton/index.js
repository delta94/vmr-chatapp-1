import React from 'react';
import './ToolbarButton.css';

export default function ToolbarButton(props) {
    const { icon } = props;
    return (
      <span className={`toolbar-button ${icon}`} onClick={props.onClick} />
    );
}