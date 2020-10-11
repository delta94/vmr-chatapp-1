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


var vmr_common_pb = require('../vmr/common_pb.js')
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
 *   !proto.vmr.Empty,
 *   !proto.vmr.FriendListResponse>}
 */
const methodDescriptor_FriendService_GetFriendList = new grpc.web.MethodDescriptor(
  '/vmr.FriendService/GetFriendList',
  grpc.web.MethodType.UNARY,
  vmr_common_pb.Empty,
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
 *   !proto.vmr.Empty,
 *   !proto.vmr.FriendListResponse>}
 */
const methodDescriptor_FriendService_GetChatFriendList = new grpc.web.MethodDescriptor(
  '/vmr.FriendService/GetChatFriendList',
  grpc.web.MethodType.UNARY,
  vmr_common_pb.Empty,
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


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.UserListRequest,
 *   !proto.vmr.UserListResponse>}
 */
const methodDescriptor_FriendService_QueryUser = new grpc.web.MethodDescriptor(
  '/vmr.FriendService/QueryUser',
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
const methodInfo_FriendService_QueryUser = new grpc.web.AbstractClientBase.MethodInfo(
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
proto.vmr.FriendServiceClient.prototype.queryUser =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.FriendService/QueryUser',
      request,
      metadata || {},
      methodDescriptor_FriendService_QueryUser,
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
proto.vmr.FriendServicePromiseClient.prototype.queryUser =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.FriendService/QueryUser',
      request,
      metadata || {},
      methodDescriptor_FriendService_QueryUser);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.ClearUnreadMessageRequest,
 *   !proto.vmr.ClearUnreadMessageResponse>}
 */
const methodDescriptor_FriendService_ClearUnreadMessage = new grpc.web.MethodDescriptor(
  '/vmr.FriendService/ClearUnreadMessage',
  grpc.web.MethodType.UNARY,
  proto.vmr.ClearUnreadMessageRequest,
  proto.vmr.ClearUnreadMessageResponse,
  /**
   * @param {!proto.vmr.ClearUnreadMessageRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.ClearUnreadMessageResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.ClearUnreadMessageRequest,
 *   !proto.vmr.ClearUnreadMessageResponse>}
 */
const methodInfo_FriendService_ClearUnreadMessage = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.ClearUnreadMessageResponse,
  /**
   * @param {!proto.vmr.ClearUnreadMessageRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.ClearUnreadMessageResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.ClearUnreadMessageRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.ClearUnreadMessageResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.ClearUnreadMessageResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.FriendServiceClient.prototype.clearUnreadMessage =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.FriendService/ClearUnreadMessage',
      request,
      metadata || {},
      methodDescriptor_FriendService_ClearUnreadMessage,
      callback);
};


/**
 * @param {!proto.vmr.ClearUnreadMessageRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.ClearUnreadMessageResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.FriendServicePromiseClient.prototype.clearUnreadMessage =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.FriendService/ClearUnreadMessage',
      request,
      metadata || {},
      methodDescriptor_FriendService_ClearUnreadMessage);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.SetFriendStatusRequest,
 *   !proto.vmr.SetFriendStatusResponse>}
 */
const methodDescriptor_FriendService_SetFriendStatus = new grpc.web.MethodDescriptor(
  '/vmr.FriendService/SetFriendStatus',
  grpc.web.MethodType.UNARY,
  proto.vmr.SetFriendStatusRequest,
  proto.vmr.SetFriendStatusResponse,
  /**
   * @param {!proto.vmr.SetFriendStatusRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.SetFriendStatusResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.SetFriendStatusRequest,
 *   !proto.vmr.SetFriendStatusResponse>}
 */
const methodInfo_FriendService_SetFriendStatus = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.SetFriendStatusResponse,
  /**
   * @param {!proto.vmr.SetFriendStatusRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.SetFriendStatusResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.SetFriendStatusRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.SetFriendStatusResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.SetFriendStatusResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.FriendServiceClient.prototype.setFriendStatus =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.FriendService/SetFriendStatus',
      request,
      metadata || {},
      methodDescriptor_FriendService_SetFriendStatus,
      callback);
};


/**
 * @param {!proto.vmr.SetFriendStatusRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.SetFriendStatusResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.FriendServicePromiseClient.prototype.setFriendStatus =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.FriendService/SetFriendStatus',
      request,
      metadata || {},
      methodDescriptor_FriendService_SetFriendStatus);
};


module.exports = proto.vmr;
