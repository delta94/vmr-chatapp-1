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
proto.vmr = require('./friend_pb.js');

/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.vmr.FriendServiceClient =
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
proto.vmr.FriendServicePromiseClient =
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
 *   !proto.vmr.AddFriendRequest,
 *   !proto.vmr.AddFriendResponse>}
 */
const methodDescriptor_FriendService_AddFriend = new grpc.web.MethodDescriptor(
  '/vmr.FriendService/AddFriend',
  grpc.web.MethodType.UNARY,
  proto.vmr.AddFriendRequest,
  proto.vmr.AddFriendResponse,
  /**
   * @param {!proto.vmr.AddFriendRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.AddFriendResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.AddFriendRequest,
 *   !proto.vmr.AddFriendResponse>}
 */
const methodInfo_FriendService_AddFriend = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.AddFriendResponse,
  /**
   * @param {!proto.vmr.AddFriendRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.AddFriendResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.AddFriendRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.AddFriendResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.AddFriendResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.FriendServiceClient.prototype.addFriend =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.FriendService/AddFriend',
      request,
      metadata || {},
      methodDescriptor_FriendService_AddFriend,
      callback);
};


/**
 * @param {!proto.vmr.AddFriendRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.AddFriendResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.FriendServicePromiseClient.prototype.addFriend =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.FriendService/AddFriend',
      request,
      metadata || {},
      methodDescriptor_FriendService_AddFriend);
};


module.exports = proto.vmr;
