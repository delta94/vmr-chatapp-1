const {SampleRequest} = require('../proto/sample_pb');
const {SampleServiceClient} = require('../proto/sample_grpc_web_pb');

const sampleClient = new SampleServiceClient("http://localhost:8083")
const sampleRequest = new SampleRequest();
sampleRequest.setContent("Hello world");

sampleClient.sampleCall(sampleRequest, {}, (err, res) => {
  if (err) {
    console.err(err);
  } else {
    console.log(res.getContent());
  }
});
