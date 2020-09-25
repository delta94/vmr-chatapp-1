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
proto.vmr = require('./user_pb.js');

/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.vmr.UserServiceClient =
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
proto.vmr.UserServicePromiseClient =
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
 *   !proto.vmr.UserListRequest,
 *   !proto.vmr.UserListResponse>}
 */
const methodDescriptor_UserService_QueryUser = new grpc.web.MethodDescriptor(
  '/vmr.UserService/QueryUser',
  grpc.web.MethodType.UNARY,
  proto.vmr.UserListRequest,
  proto.vmr.UserListResponse,
  /**
   * @param {!proto.vmr.UserListRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.UserListResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.UserListRequest,
 *   !proto.vmr.UserListResponse>}
 */
const methodInfo_UserService_QueryUser = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.UserListResponse,
  /**
   * @param {!proto.vmr.UserListRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.UserListResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.UserListRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.UserListResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.UserListResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.UserServiceClient.prototype.queryUser =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.UserService/QueryUser',
      request,
      metadata || {},
      methodDescriptor_UserService_QueryUser,
      callback);
};


/**
 * @param {!proto.vmr.UserListRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.UserListResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.UserServicePromiseClient.prototype.queryUser =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.UserService/QueryUser',
      request,
      metadata || {},
      methodDescriptor_UserService_QueryUser);
};


module.exports = proto.vmr;
