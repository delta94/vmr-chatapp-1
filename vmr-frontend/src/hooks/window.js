import {useEffect, useState} from "react";

export default function useWindowSize() {
  let [size, setSize] = useState(window.innerWidth);

  useEffect(() => {
    let listener = () => {
      setSize(window.innerWidth);
    };
    window.addEventListener('resize', listener);

    return () => {
      window.removeEventListener('resize', listener);
    };
  }, []);

  return size;
}
