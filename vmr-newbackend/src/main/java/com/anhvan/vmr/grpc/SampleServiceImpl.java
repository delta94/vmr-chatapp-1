package com.anhvan.vmr.grpc;

import com.anhvan.vmr.proto.Sample.SampleRequest;
import com.anhvan.vmr.proto.Sample.SampleResponse;
import com.anhvan.vmr.proto.SampleServiceGrpc.SampleServiceImplBase;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Log4j2
public class SampleServiceImpl extends SampleServiceImplBase {
  @Inject
  public SampleServiceImpl() {}

  @Override
  public void sampleCall(SampleRequest request, StreamObserver<SampleResponse> responseObserver) {
    SampleResponse response =
        SampleResponse.newBuilder().setContent(request.getContent().toUpperCase()).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
