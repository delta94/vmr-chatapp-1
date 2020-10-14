package com.anhvan.vmr.grpc;

import com.anhvan.vmr.proto.Sample.SampleRequest;
import com.anhvan.vmr.proto.Sample.SampleResponse;
import com.anhvan.vmr.proto.SampleServiceGrpc.SampleServiceImplBase;
import io.grpc.stub.StreamObserver;

import javax.inject.Singleton;

@Singleton
public class GrpcSampleServiceImpl extends SampleServiceImplBase {
  @Override
  public void sampleCall(SampleRequest request, StreamObserver<SampleResponse> responseObserver) {
    SampleResponse response =
        SampleResponse.newBuilder().setContent(request.getContent().toUpperCase()).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void sampleStreamCall(
      SampleRequest request, StreamObserver<SampleResponse> responseObserver) {
    for (int i = 0; i < 10; ++i) {
      responseObserver.onNext(SampleResponse.newBuilder().setContent(String.valueOf(i)).build());
    }
    responseObserver.onCompleted();
  }
}
