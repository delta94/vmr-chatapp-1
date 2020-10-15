import React from 'react';
import {Spin} from "antd";

import './LoadingArea.css';

export default function LoadingArea() {
  return (
    <div className={'loading-area'}>
      <Spin size={'large'}/>
    </div>
  )
}