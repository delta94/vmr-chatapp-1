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


var google_protobuf_empty_pb = require('google-protobuf/google/protobuf/empty_pb.js')

var vmr_transfer_status_pb = require('../vmr/transfer_status_pb.js')
const proto = {};
proto.vmr = require('./history_pb.js');

/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.vmr.HistoryServiceClient =
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
proto.vmr.HistoryServicePromiseClient =
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
 *   !proto.google.protobuf.Empty,
 *   !proto.vmr.HistoryResponse>}
 */
const methodDescriptor_HistoryService_getHistory = new grpc.web.MethodDescriptor(
  '/vmr.HistoryService/getHistory',
  grpc.web.MethodType.SERVER_STREAMING,
  google_protobuf_empty_pb.Empty,
  proto.vmr.HistoryResponse,
  /**
   * @param {!proto.google.protobuf.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.HistoryResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.google.protobuf.Empty,
 *   !proto.vmr.HistoryResponse>}
 */
const methodInfo_HistoryService_getHistory = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.HistoryResponse,
  /**
   * @param {!proto.google.protobuf.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.HistoryResponse.deserializeBinary
);


/**
 * @param {!proto.google.protobuf.Empty} request The request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.HistoryResponse>}
 *     The XHR Node Readable Stream
 */
proto.vmr.HistoryServiceClient.prototype.getHistory =
    function(request, metadata) {
  return this.client_.serverStreaming(this.hostname_ +
      '/vmr.HistoryService/getHistory',
      request,
      metadata || {},
      methodDescriptor_HistoryService_getHistory);
};


/**
 * @param {!proto.google.protobuf.Empty} request The request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.HistoryResponse>}
 *     The XHR Node Readable Stream
 */
proto.vmr.HistoryServicePromiseClient.prototype.getHistory =
    function(request, metadata) {
  return this.client_.serverStreaming(this.hostname_ +
      '/vmr.HistoryService/getHistory',
      request,
      metadata || {},
      methodDescriptor_HistoryService_getHistory);
};


module.exports = proto.vmr;
