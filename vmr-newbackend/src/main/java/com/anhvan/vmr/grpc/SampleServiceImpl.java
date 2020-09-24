package com.anhvan.vmr.grpc;

import com.anhvan.vmr.proto.Sample.SampleRequest;
import com.anhvan.vmr.proto.Sample.SampleResponse;
import com.anhvan.vmr.proto.SampleServiceGrpc.SampleServiceImplBase;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;

import javax.inject.Singleton;

@Singleton
@Log4j2
public class SampleServiceImpl extends SampleServiceImplBase {
  @Override
  public void sampleCall(SampleRequest request, StreamObserver<SampleResponse> responseObserver) {
    log.debug("Handle sampleCall grpc request");
    SampleResponse response =
        SampleResponse.newBuilder().setContent(request.getContent().toUpperCase()).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void sampleStreamCall(
      SampleRequest request, StreamObserver<SampleResponse> responseObserver) {
    log.debug("Get message " + request.getContent());
    for (int i = 0; i < 10; ++i) {
      responseObserver.onNext(SampleResponse.newBuilder().setContent(String.valueOf(i)).build());
    }
    responseObserver.onCompleted();
  }
}
