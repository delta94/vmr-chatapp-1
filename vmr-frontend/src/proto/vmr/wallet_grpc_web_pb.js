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
proto.vmr = require('./wallet_pb.js');

/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.vmr.WalletServiceClient =
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
proto.vmr.WalletServicePromiseClient =
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
 *   !proto.vmr.BalanceResponse>}
 */
const methodDescriptor_WalletService_GetBalance = new grpc.web.MethodDescriptor(
  '/vmr.WalletService/GetBalance',
  grpc.web.MethodType.UNARY,
  vmr_common_pb.Empty,
  proto.vmr.BalanceResponse,
  /**
   * @param {!proto.vmr.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.BalanceResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.Empty,
 *   !proto.vmr.BalanceResponse>}
 */
const methodInfo_WalletService_GetBalance = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.BalanceResponse,
  /**
   * @param {!proto.vmr.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.BalanceResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.BalanceResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.BalanceResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.WalletServiceClient.prototype.getBalance =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.WalletService/GetBalance',
      request,
      metadata || {},
      methodDescriptor_WalletService_GetBalance,
      callback);
};


/**
 * @param {!proto.vmr.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.BalanceResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.WalletServicePromiseClient.prototype.getBalance =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.WalletService/GetBalance',
      request,
      metadata || {},
      methodDescriptor_WalletService_GetBalance);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.TransferRequest,
 *   !proto.vmr.TransferResponse>}
 */
const methodDescriptor_WalletService_Transfer = new grpc.web.MethodDescriptor(
  '/vmr.WalletService/Transfer',
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
const methodInfo_WalletService_Transfer = new grpc.web.AbstractClientBase.MethodInfo(
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
proto.vmr.WalletServiceClient.prototype.transfer =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.WalletService/Transfer',
      request,
      metadata || {},
      methodDescriptor_WalletService_Transfer,
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
proto.vmr.WalletServicePromiseClient.prototype.transfer =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.WalletService/Transfer',
      request,
      metadata || {},
      methodDescriptor_WalletService_Transfer);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.Empty,
 *   !proto.vmr.HistoryResponse>}
 */
const methodDescriptor_WalletService_GetHistory = new grpc.web.MethodDescriptor(
  '/vmr.WalletService/GetHistory',
  grpc.web.MethodType.UNARY,
  vmr_common_pb.Empty,
  proto.vmr.HistoryResponse,
  /**
   * @param {!proto.vmr.Empty} request
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
 *   !proto.vmr.Empty,
 *   !proto.vmr.HistoryResponse>}
 */
const methodInfo_WalletService_GetHistory = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.HistoryResponse,
  /**
   * @param {!proto.vmr.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.HistoryResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.HistoryResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.HistoryResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.WalletServiceClient.prototype.getHistory =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.WalletService/GetHistory',
      request,
      metadata || {},
      methodDescriptor_WalletService_GetHistory,
      callback);
};


/**
 * @param {!proto.vmr.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.HistoryResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.WalletServicePromiseClient.prototype.getHistory =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.WalletService/GetHistory',
      request,
      metadata || {},
      methodDescriptor_WalletService_GetHistory);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.GetHistoryWithOffsetRequest,
 *   !proto.vmr.HistoryResponse>}
 */
const methodDescriptor_WalletService_GetHistoryWithOffset = new grpc.web.MethodDescriptor(
  '/vmr.WalletService/GetHistoryWithOffset',
  grpc.web.MethodType.UNARY,
  proto.vmr.GetHistoryWithOffsetRequest,
  proto.vmr.HistoryResponse,
  /**
   * @param {!proto.vmr.GetHistoryWithOffsetRequest} request
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
 *   !proto.vmr.GetHistoryWithOffsetRequest,
 *   !proto.vmr.HistoryResponse>}
 */
const methodInfo_WalletService_GetHistoryWithOffset = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.HistoryResponse,
  /**
   * @param {!proto.vmr.GetHistoryWithOffsetRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.HistoryResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.GetHistoryWithOffsetRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.HistoryResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.HistoryResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.WalletServiceClient.prototype.getHistoryWithOffset =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.WalletService/GetHistoryWithOffset',
      request,
      metadata || {},
      methodDescriptor_WalletService_GetHistoryWithOffset,
      callback);
};


/**
 * @param {!proto.vmr.GetHistoryWithOffsetRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.HistoryResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.WalletServicePromiseClient.prototype.getHistoryWithOffset =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.WalletService/GetHistoryWithOffset',
      request,
      metadata || {},
      methodDescriptor_WalletService_GetHistoryWithOffset);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.TransferReminderRequest,
 *   !proto.vmr.TransferReminderResponse>}
 */
const methodDescriptor_WalletService_RemindTransfer = new grpc.web.MethodDescriptor(
  '/vmr.WalletService/RemindTransfer',
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
const methodInfo_WalletService_RemindTransfer = new grpc.web.AbstractClientBase.MethodInfo(
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
proto.vmr.WalletServiceClient.prototype.remindTransfer =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.WalletService/RemindTransfer',
      request,
      metadata || {},
      methodDescriptor_WalletService_RemindTransfer,
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
proto.vmr.WalletServicePromiseClient.prototype.remindTransfer =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.WalletService/RemindTransfer',
      request,
      metadata || {},
      methodDescriptor_WalletService_RemindTransfer);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.vmr.Empty,
 *   !proto.vmr.TransferReminderListResponse>}
 */
const methodDescriptor_WalletService_GetTransferReminder = new grpc.web.MethodDescriptor(
  '/vmr.WalletService/GetTransferReminder',
  grpc.web.MethodType.UNARY,
  vmr_common_pb.Empty,
  proto.vmr.TransferReminderListResponse,
  /**
   * @param {!proto.vmr.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.TransferReminderListResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.vmr.Empty,
 *   !proto.vmr.TransferReminderListResponse>}
 */
const methodInfo_WalletService_GetTransferReminder = new grpc.web.AbstractClientBase.MethodInfo(
  proto.vmr.TransferReminderListResponse,
  /**
   * @param {!proto.vmr.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.vmr.TransferReminderListResponse.deserializeBinary
);


/**
 * @param {!proto.vmr.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.vmr.TransferReminderListResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.vmr.TransferReminderListResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.vmr.WalletServiceClient.prototype.getTransferReminder =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/vmr.WalletService/GetTransferReminder',
      request,
      metadata || {},
      methodDescriptor_WalletService_GetTransferReminder,
      callback);
};


/**
 * @param {!proto.vmr.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.vmr.TransferReminderListResponse>}
 *     Promise that resolves to the response
 */
proto.vmr.WalletServicePromiseClient.prototype.getTransferReminder =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/vmr.WalletService/GetTransferReminder',
      request,
      metadata || {},
      methodDescriptor_WalletService_GetTransferReminder);
};


module.exports = proto.vmr;
