const {SampleRequest} = require('../proto/sample_pb');
const {SampleServiceClient} = require('../proto/sample_grpc_web_pb');

const sampleClient = new SampleServiceClient("http://localhost:8083", null, null);
const sampleRequest = new SampleRequest();
sampleRequest.setContent("Hello world");

sampleClient.sampleCall(sampleRequest, {}, (err, res) => {
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
