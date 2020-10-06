import {useEffect, useState} from "react";
import {getBalance} from "../service/wallet";

export function useBalance(...dependencies) {
  let [balance, setBalance] = useState(0);
  useEffect(() => {
    getBalance().then(result => {
      console.log(result.getBalance());
      setBalance(result.getBalance());
    });
    // eslint-disable-next-line 
  }, [...dependencies])
  return balance;
}