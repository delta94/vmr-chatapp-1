import {getGrpcTokenMetadata} from "../util/auth-util";

const {SampleRequest} = require('../proto/vmr/sample_pb');
const {SampleServiceClient} = require('../proto/vmr/sample_grpc_web_pb');

const sampleClient = new SampleServiceClient('http://localhost:8083', null, null);

const sampleRequest = new SampleRequest();
sampleRequest.setContent('Hello world');

sampleClient.sampleCall(sampleRequest, getGrpcTokenMetadata(), (err, res) => {
  if (err) {
    console.log(err);
  } else {
    console.log(res.getContent());
  }
});

let stream = sampleClient.sampleStreamCall(sampleRequest, getGrpcTokenMetadata());
stream.on('data', response => {
  console.log(response.getContent());
});
stream.on('error', error => {
  console.log(error);
});