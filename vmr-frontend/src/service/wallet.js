import {getGrpcTokenMetadata} from "../util/auth-util";

const {Empty} = require('../proto/vmr/common_pb');
const {TransferRequest} = require('../proto/vmr/wallet_pb');
const {WalletServiceClient} = require('../proto/vmr/wallet_grpc_web_pb');

const ENVOY_ROOT = process.env.REACT_APP_ENVOY_ROOT;

let wallet = new WalletServiceClient(ENVOY_ROOT, null, null);

export function getBalance() {
  return new Promise((resolve, reject) => {
    wallet.getBalance(new Empty(), getGrpcTokenMetadata(), (err, res) => {
      if (err) {
        reject(err);
      } else {
        if (res.getError()) {
          reject(res.getError());
        } else {
          resolve(res.getData());
        }
      }
    });
  });
}

export function getHistory() {
  return new Promise((resolve, reject) => {
    wallet.getHistory(new Empty(), getGrpcTokenMetadata(), (err, res) => {
      if (err) {
        console.log(err);
        reject(err);
      } else {
        if (res.getError()) {
          console.log(res.getError());
          reject(res.getError());
        } else {
          console.log(res.getItemList());
          resolve(res.getItemList());
        }
      }
    });
  });
}

export function transfer(receiver, amount, password, message, requestId) {
  return new Promise((resolve, reject) => {
    let transferRequest = new TransferRequest();

    transferRequest.setReceiver(receiver);
    transferRequest.setAmount(amount);
    transferRequest.setPassword(password);
    transferRequest.setMessage(message);
    transferRequest.setRequestId(requestId)

    wallet.transfer(transferRequest, getGrpcTokenMetadata(), (err, res) => {
      if (err) {
        console.log(err);
        reject(err);
      } else {
        if (res.getError()) {
          console.log(res.getError());
          reject(res.getError());
        } else {
          console.log(res.getData());
          resolve(res.getData());
        }
      }
    });
  });
}