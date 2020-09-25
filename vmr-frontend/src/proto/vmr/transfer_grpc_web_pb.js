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


var vmr_transfer_status_pb = require('../vmr/transfer_status_pb.js')
const proto = {};
proto.vmr = require('./transfer_pb.js');

/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.vmr.TransferServiceClient =
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
proto.vmr.TransferServicePromiseClient =
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
 *   !proto.vmr.TransferRequest,
 *   !proto.vmr.TransferResponse>}
 */
const methodDescriptor_TransferService_Transfer = new grpc.web.MethodDescriptor(
  '/vmr.TransferService/Transfer',
  grpc.web.MethodType.UNARY,
  proto.vmr.TransferRequest,
  proto.vmr.TransferResponse,
  /**
   * @param {!proto.vmr.TransferRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.TransferResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.TransferRequest,
 *   !proto.vmr.TransferResponse>}
 */
const methodInfo_TransferService_Transfer = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.TransferResponse,
  /**
   * @param {!proto.vmr.TransferRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.TransferResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.TransferRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.TransferResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.TransferResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.TransferServiceClient.prototype.transfer =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.TransferService/Transfer',
      request,
      metadata || {},
      methodDescriptor_TransferService_Transfer,
      callback);
};


/**
 * @param {!proto.vmr.TransferRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.TransferResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.TransferServicePromiseClient.prototype.transfer =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.TransferService/Transfer',
      request,
      metadata || {},
      methodDescriptor_TransferService_Transfer);
};


module.exports = proto.vmr;
