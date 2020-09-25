import {getJwtToken} from "../util/auth-util";

const {SampleRequest} = require('../proto/vmr/sample_pb');
const {SampleServiceClient} = require('../proto/vmr/sample_grpc_web_pb');
const {UserListRequest} = require('../proto/vmr/user_pb');
const {UserServiceClient} = require('../proto/vmr/user_grpc_web_pb');

const sampleClient = new SampleServiceClient('http://localhost:8083', null, null);
const userClient = new UserServiceClient('http://localhost:8083', null, null);
const sampleRequest = new SampleRequest();
sampleRequest.setContent('Hello world');

let token = getJwtToken();
console.log(token);
sampleClient.sampleCall(sampleRequest, {'x-jwt-token': token}, (err, res) => {
  if (err) {
    console.log(err);
  } else {
    console.log(res.getContent());
  }
});

let stream = sampleClient.sampleStreamCall(sampleRequest, {'x-jwt-token': token});
stream.on('data', response => {
  console.log(response.getContent());
});
stream.on('error', error => {
  console.log(error);
});

let userListRq = new UserListRequest();
userListRq.setQueryString("vovanduc");
userClient.queryUser(userListRq, {'x-jwt-token': token}, (err, res) => {
  if (err) {
    console.log(err);
  } else {
    console.log(res.getUserList()[0].getName());
  }
});
