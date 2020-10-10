import React, {useEffect} from 'react';
import {useOpenSideBar} from "../../hooks/ui";

export default function EmptyPage() {
  let openSideBar = useOpenSideBar();
  useEffect(() => {
    openSideBar();
  });
  return (
    <div/>
  );
}