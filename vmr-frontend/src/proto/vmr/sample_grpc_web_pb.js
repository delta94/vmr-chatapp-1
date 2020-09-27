/* eslint-disable */ /**
 * @fileoverview gRPC-Web generated client stub for vmr
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck



const grpc = {};
grpc.web = require('grpc-web');

const proto = {};
proto.vmr = require('./sample_pb.js');

/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.vmr.SampleServiceClient =
    function(hostname, credentials, options) {
  if (!options) options = {};
  options['format'] = 'text';

  /**
   * @private @const {!grpc.web.GrpcWebClientBase} The client
   */
  this.client_ = new grpc.web.GrpcWebClientBase(options);

  /**
   * @private @const {string} The hostname
   */
  this.hostname_ = hostname;

};


/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.vmr.SampleServicePromiseClient =
    function(hostname, credentials, options) {
  if (!options) options = {};
  options['format'] = 'text';

  /**
   * @private @const {!grpc.web.GrpcWebClientBase} The client
   */
  this.client_ = new grpc.web.GrpcWebClientBase(options);

  /**
   * @private @const {string} The hostname
   */
  this.hostname_ = hostname;

};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.SampleRequest,
 *   !proto.vmr.SampleResponse>}
 */
const methodDescriptor_SampleService_SampleCall = new grpc.web.MethodDescriptor(
  '/vmr.SampleService/SampleCall',
  grpc.web.MethodType.UNARY,
  proto.vmr.SampleRequest,
  proto.vmr.SampleResponse,
  /**
   * @param {!proto.vmr.SampleRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.SampleResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.SampleRequest,
 *   !proto.vmr.SampleResponse>}
 */
const methodInfo_SampleService_SampleCall = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.SampleResponse,
  /**
   * @param {!proto.vmr.SampleRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.SampleResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.SampleRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.SampleResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.SampleResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.SampleServiceClient.prototype.sampleCall =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.SampleService/SampleCall',
      request,
      metadata || {},
      methodDescriptor_SampleService_SampleCall,
      callback);
};


/**
 * @param {!proto.vmr.SampleRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.SampleResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.SampleServicePromiseClient.prototype.sampleCall =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.SampleService/SampleCall',
      request,
      metadata || {},
      methodDescriptor_SampleService_SampleCall);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.SampleRequest,
 *   !proto.vmr.SampleResponse>}
 */
const methodDescriptor_SampleService_SampleStreamCall = new grpc.web.MethodDescriptor(
  '/vmr.SampleService/SampleStreamCall',
  grpc.web.MethodType.SERVER_STREAMING,
  proto.vmr.SampleRequest,
  proto.vmr.SampleResponse,
  /**
   * @param {!proto.vmr.SampleRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.SampleResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.SampleRequest,
 *   !proto.vmr.SampleResponse>}
 */
const methodInfo_SampleService_SampleStreamCall = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.SampleResponse,
  /**
   * @param {!proto.vmr.SampleRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.SampleResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.SampleRequest} request The request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.SampleResponse>}
 *     The XHR Node Readable Stream
 */
proto.vmr.SampleServiceClient.prototype.sampleStreamCall =
    function(request, metadata) {
  return this.client_.serverStreaming(this.hostname_ +
      '/vmr.SampleService/SampleStreamCall',
      request,
      metadata || {},
      methodDescriptor_SampleService_SampleStreamCall);
};


/**
 * @param {!proto.vmr.SampleRequest} request The request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.SampleResponse>}
 *     The XHR Node Readable Stream
 */
proto.vmr.SampleServicePromiseClient.prototype.sampleStreamCall =
    function(request, metadata) {
  return this.client_.serverStreaming(this.hostname_ +
      '/vmr.SampleService/SampleStreamCall',
      request,
      metadata || {},
      methodDescriptor_SampleService_SampleStreamCall);
};


module.exports = proto.vmr;
