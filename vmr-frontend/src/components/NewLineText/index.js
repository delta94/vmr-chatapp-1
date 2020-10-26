import React from 'react';

import './NewLineText.css';

export default function NewLineText(props) {
  let {text} = props;
  return (
    <>
      {text.split(/\n+/).map((x, i) => <p key={i} className={'no-margin'}>{x}</p>)}
    </>
  );
}