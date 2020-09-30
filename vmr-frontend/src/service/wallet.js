import {getGrpcTokenMetadata} from "../util/auth-util";
const {Empty} = require('../proto/vmr/common_pb');
const {WalletServiceClient} = require('../proto/vmr/wallet_grpc_web_pb');

const ENVOY_ROOT = process.env.REACT_APP_ENVOY_ROOT;

let wallet = new WalletServiceClient(ENVOY_ROOT, null, null);

export function getBalance() {
  return new Promise((resolve, reject) => {
    wallet.getBalance(new Empty(), getGrpcTokenMetadata(), (err, res) => {
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
