import {useEffect, useState} from "react";
import {getBalance} from "../service/wallet";

export function useBalance(...dependencies) {
  let [balance, setBalance] = useState(0);
  useEffect(() => {
    getBalance().then(result => {
      setBalance(result.getBalance());
    });
    // eslint-disable-next-line
  }, [...dependencies])
  return balance;
}

export function useInfoWithBalance(...dependencies) {
  let [info, setInfo] = useState({});
  useEffect(() => {
    getBalance().then(result => {
      setInfo({
        balance: result.getBalance(),
        userName: result.getUserName(),
        lastUpdated: result.getLastUpdated(),
        name: result.getName()
      });
    });
    // eslint-disable-next-line
  }, [...dependencies]);
  return info;
}