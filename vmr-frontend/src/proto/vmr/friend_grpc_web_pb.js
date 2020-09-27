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

var vmr_empty_pb = require('../vmr/empty_pb.js')
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


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.Empty,
 *   !proto.vmr.FriendListResponse>}
 */
const methodDescriptor_FriendService_GetFriendList = new grpc.web.MethodDescriptor(
  '/vmr.FriendService/GetFriendList',
  grpc.web.MethodType.UNARY,
  vmr_empty_pb.Empty,
  proto.vmr.FriendListResponse,
  /**
   * @param {!proto.vmr.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.FriendListResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.Empty,
 *   !proto.vmr.FriendListResponse>}
 */
const methodInfo_FriendService_GetFriendList = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.FriendListResponse,
  /**
   * @param {!proto.vmr.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.FriendListResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.FriendListResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.FriendListResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.FriendServiceClient.prototype.getFriendList =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.FriendService/GetFriendList',
      request,
      metadata || {},
      methodDescriptor_FriendService_GetFriendList,
      callback);
};


/**
 * @param {!proto.vmr.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.FriendListResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.FriendServicePromiseClient.prototype.getFriendList =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.FriendService/GetFriendList',
      request,
      metadata || {},
      methodDescriptor_FriendService_GetFriendList);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.AcceptFriendRequest,
 *   !proto.vmr.AcceptFriendResponse>}
 */
const methodDescriptor_FriendService_AcceptFriend = new grpc.web.MethodDescriptor(
  '/vmr.FriendService/AcceptFriend',
  grpc.web.MethodType.UNARY,
  proto.vmr.AcceptFriendRequest,
  proto.vmr.AcceptFriendResponse,
  /**
   * @param {!proto.vmr.AcceptFriendRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.AcceptFriendResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.AcceptFriendRequest,
 *   !proto.vmr.AcceptFriendResponse>}
 */
const methodInfo_FriendService_AcceptFriend = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.AcceptFriendResponse,
  /**
   * @param {!proto.vmr.AcceptFriendRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.AcceptFriendResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.AcceptFriendRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.AcceptFriendResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.AcceptFriendResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.FriendServiceClient.prototype.acceptFriend =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.FriendService/AcceptFriend',
      request,
      metadata || {},
      methodDescriptor_FriendService_AcceptFriend,
      callback);
};


/**
 * @param {!proto.vmr.AcceptFriendRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.AcceptFriendResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.FriendServicePromiseClient.prototype.acceptFriend =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.FriendService/AcceptFriend',
      request,
      metadata || {},
      methodDescriptor_FriendService_AcceptFriend);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.RejectFriendRequest,
 *   !proto.vmr.RejectFriendResponse>}
 */
const methodDescriptor_FriendService_RejectFriend = new grpc.web.MethodDescriptor(
  '/vmr.FriendService/RejectFriend',
  grpc.web.MethodType.UNARY,
  proto.vmr.RejectFriendRequest,
  proto.vmr.RejectFriendResponse,
  /**
   * @param {!proto.vmr.RejectFriendRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.RejectFriendResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.RejectFriendRequest,
 *   !proto.vmr.RejectFriendResponse>}
 */
const methodInfo_FriendService_RejectFriend = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.RejectFriendResponse,
  /**
   * @param {!proto.vmr.RejectFriendRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.RejectFriendResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.RejectFriendRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.RejectFriendResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.RejectFriendResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.FriendServiceClient.prototype.rejectFriend =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.FriendService/RejectFriend',
      request,
      metadata || {},
      methodDescriptor_FriendService_RejectFriend,
      callback);
};


/**
 * @param {!proto.vmr.RejectFriendRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.RejectFriendResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.FriendServicePromiseClient.prototype.rejectFriend =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.FriendService/RejectFriend',
      request,
      metadata || {},
      methodDescriptor_FriendService_RejectFriend);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.Empty,
 *   !proto.vmr.FriendListResponse>}
 */
const methodDescriptor_FriendService_GetChatFriendList = new grpc.web.MethodDescriptor(
  '/vmr.FriendService/GetChatFriendList',
  grpc.web.MethodType.UNARY,
  vmr_empty_pb.Empty,
  proto.vmr.FriendListResponse,
  /**
   * @param {!proto.vmr.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.FriendListResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.Empty,
 *   !proto.vmr.FriendListResponse>}
 */
const methodInfo_FriendService_GetChatFriendList = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.FriendListResponse,
  /**
   * @param {!proto.vmr.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.FriendListResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.FriendListResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.FriendListResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.FriendServiceClient.prototype.getChatFriendList =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.FriendService/GetChatFriendList',
      request,
      metadata || {},
      methodDescriptor_FriendService_GetChatFriendList,
      callback);
};


/**
 * @param {!proto.vmr.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.FriendListResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.FriendServicePromiseClient.prototype.getChatFriendList =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.FriendService/GetChatFriendList',
      request,
      metadata || {},
      methodDescriptor_FriendService_GetChatFriendList);
};


module.exports = proto.vmr;
