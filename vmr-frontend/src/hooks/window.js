import {useEffect, useState} from "react";

export default function useWindowSize() {
  let [size, setSize] = useState(window.innerWidth);

  useEffect(() => {
    window.addEventListener('resize', () => {
      setSize(window.innerWidth);
    });
  }, []);

  return size;
}
