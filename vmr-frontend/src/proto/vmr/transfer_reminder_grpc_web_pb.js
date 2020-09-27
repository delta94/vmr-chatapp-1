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


var vmr_error_pb = require('../vmr/error_pb.js')
const proto = {};
proto.vmr = require('./transfer_reminder_pb.js');

/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.vmr.TransferReminderServiceClient =
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
proto.vmr.TransferReminderServicePromiseClient =
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
 *   !proto.vmr.TransferReminderRequest,
 *   !proto.vmr.TransferReminderResponse>}
 */
const methodDescriptor_TransferReminderService_RemindTransfer = new grpc.web.MethodDescriptor(
  '/vmr.TransferReminderService/RemindTransfer',
  grpc.web.MethodType.UNARY,
  proto.vmr.TransferReminderRequest,
  proto.vmr.TransferReminderResponse,
  /**
   * @param {!proto.vmr.TransferReminderRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.TransferReminderResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.TransferReminderRequest,
 *   !proto.vmr.TransferReminderResponse>}
 */
const methodInfo_TransferReminderService_RemindTransfer = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.TransferReminderResponse,
  /**
   * @param {!proto.vmr.TransferReminderRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.TransferReminderResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.TransferReminderRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.TransferReminderResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.TransferReminderResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.TransferReminderServiceClient.prototype.remindTransfer =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.TransferReminderService/RemindTransfer',
      request,
      metadata || {},
      methodDescriptor_TransferReminderService_RemindTransfer,
      callback);
};


/**
 * @param {!proto.vmr.TransferReminderRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.TransferReminderResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.TransferReminderServicePromiseClient.prototype.remindTransfer =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.TransferReminderService/RemindTransfer',
      request,
      metadata || {},
      methodDescriptor_TransferReminderService_RemindTransfer);
};


module.exports = proto.vmr;
