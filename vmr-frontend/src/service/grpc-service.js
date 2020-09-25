import {getJwtToken} from "../util/auth-util";

const {SampleRequest} = require('../proto/sample_pb');
const {SampleServiceClient} = require('../proto/sample_grpc_web_pb');

const sampleClient = new SampleServiceClient('http://localhost:8083', null, null);
const sampleRequest = new SampleRequest();
sampleRequest.setContent('Hello world');

let token = getJwtToken();
console.log(token);
sampleClient.sampleCall(sampleRequest, {'x-jwt-token': token.substring(0, token.length-1)}, (err, res) => {
  if (err) {
    console.log(err);
  } else {
    console.log(res.getContent());
  }
});

let stream = sampleClient.sampleStreamCall(sampleRequest, {});
stream.on('data', response => {
  console.log(response.getContent());
});
stream.on('error', error => {
  console.log(error);
});
