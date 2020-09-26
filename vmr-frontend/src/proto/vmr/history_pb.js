/* eslint-disable */ /**
 * @fileoverview
 * @enhanceable
 * @suppress {messageConventions} JS Compiler reports an error if a variable or
 *     field starts with 'MSG_' and isn't a translatable message.
 * @public
 */
// GENERATED CODE -- DO NOT EDIT!

var jspb = require('google-protobuf');
var goog = jspb;
var global = Function('return this')();

var google_protobuf_empty_pb = require('google-protobuf/google/protobuf/empty_pb.js');
var vmr_error_pb = require('../vmr/error_pb.js');
goog.exportSymbol('proto.vmr.HistoryListResponse', null, global);
goog.exportSymbol('proto.vmr.HistoryResponse', null, global);
goog.exportSymbol('proto.vmr.HistoryResponseType', null, global);
goog.exportSymbol('proto.vmr.ReceiveHistoryResponse', null, global);
goog.exportSymbol('proto.vmr.TransferHistoryResponse', null, global);
goog.exportSymbol('proto.vmr.TransferHistoryResponse.TransferStatus', null, global);

/**
 * Generated by JsPbCodeGenerator.
 * @param {Array=} opt_data Optional initial data array, typically from a
 * server response, or constructed directly in Javascript. The array is used
 * in place and becomes part of the constructed object. It is not cloned.
 * If no data is provided, the constructed object will be empty, but still
 * valid.
 * @extends {jspb.Message}
 * @constructor
 */
proto.vmr.TransferHistoryResponse = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, null, null);
};
goog.inherits(proto.vmr.TransferHistoryResponse, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.vmr.TransferHistoryResponse.displayName = 'proto.vmr.TransferHistoryResponse';
}


if (jspb.Message.GENERATE_TO_OBJECT) {
/**
 * Creates an object representation of this proto suitable for use in Soy templates.
 * Field names that are reserved in JavaScript and will be renamed to pb_name.
 * To access a reserved field use, foo.pb_<name>, eg, foo.pb_default.
 * For the list of reserved names please see:
 *     com.google.apps.jspb.JsClassTemplate.JS_RESERVED_WORDS.
 * @param {boolean=} opt_includeInstance Whether to include the JSPB instance
 *     for transitional soy proto support: http://goto/soy-param-migration
 * @return {!Object}
 */
proto.vmr.TransferHistoryResponse.prototype.toObject = function(opt_includeInstance) {
  return proto.vmr.TransferHistoryResponse.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.vmr.TransferHistoryResponse} msg The msg instance to transform.
 * @return {!Object}
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.vmr.TransferHistoryResponse.toObject = function(includeInstance, msg) {
  var f, obj = {
    to: jspb.Message.getFieldWithDefault(msg, 1, 0),
    timestamp: jspb.Message.getFieldWithDefault(msg, 2, 0),
    amount: jspb.Message.getFieldWithDefault(msg, 3, 0),
    transferStatus: jspb.Message.getFieldWithDefault(msg, 4, 0)
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg;
  }
  return obj;
};
}


/**
 * Deserializes binary data (in protobuf wire format).
 * @param {jspb.ByteSource} bytes The bytes to deserialize.
 * @return {!proto.vmr.TransferHistoryResponse}
 */
proto.vmr.TransferHistoryResponse.deserializeBinary = function(bytes) {
  var reader = new jspb.BinaryReader(bytes);
  var msg = new proto.vmr.TransferHistoryResponse;
  return proto.vmr.TransferHistoryResponse.deserializeBinaryFromReader(msg, reader);
};


/**
 * Deserializes binary data (in protobuf wire format) from the
 * given reader into the given message object.
 * @param {!proto.vmr.TransferHistoryResponse} msg The message object to deserialize into.
 * @param {!jspb.BinaryReader} reader The BinaryReader to use.
 * @return {!proto.vmr.TransferHistoryResponse}
 */
proto.vmr.TransferHistoryResponse.deserializeBinaryFromReader = function(msg, reader) {
  while (reader.nextField()) {
    if (reader.isEndGroup()) {
      break;
    }
    var field = reader.getFieldNumber();
    switch (field) {
    case 1:
      var value = /** @type {number} */ (reader.readInt64());
      msg.setTo(value);
      break;
    case 2:
      var value = /** @type {number} */ (reader.readInt64());
      msg.setTimestamp(value);
      break;
    case 3:
      var value = /** @type {number} */ (reader.readInt64());
      msg.setAmount(value);
      break;
    case 4:
      var value = /** @type {!proto.vmr.TransferHistoryResponse.TransferStatus} */ (reader.readEnum());
      msg.setTransferStatus(value);
      break;
    default:
      reader.skipField();
      break;
    }
  }
  return msg;
};


/**
 * Serializes the message to binary data (in protobuf wire format).
 * @return {!Uint8Array}
 */
proto.vmr.TransferHistoryResponse.prototype.serializeBinary = function() {
  var writer = new jspb.BinaryWriter();
  proto.vmr.TransferHistoryResponse.serializeBinaryToWriter(this, writer);
  return writer.getResultBuffer();
};


/**
 * Serializes the given message to binary data (in protobuf wire
 * format), writing to the given BinaryWriter.
 * @param {!proto.vmr.TransferHistoryResponse} message
 * @param {!jspb.BinaryWriter} writer
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.vmr.TransferHistoryResponse.serializeBinaryToWriter = function(message, writer) {
  var f = undefined;
  f = message.getTo();
  if (f !== 0) {
    writer.writeInt64(
      1,
      f
    );
  }
  f = message.getTimestamp();
  if (f !== 0) {
    writer.writeInt64(
      2,
      f
    );
  }
  f = message.getAmount();
  if (f !== 0) {
    writer.writeInt64(
      3,
      f
    );
  }
  f = message.getTransferStatus();
  if (f !== 0.0) {
    writer.writeEnum(
      4,
      f
    );
  }
};


/**
 * @enum {number}
 */
proto.vmr.TransferHistoryResponse.TransferStatus = {
  TRANSFER_SUCCESS: 0,
  TRANSFER_FAILUE: 1
};

/**
 * optional int64 to = 1;
 * @return {number}
 */
proto.vmr.TransferHistoryResponse.prototype.getTo = function() {
  return /** @type {number} */ (jspb.Message.getFieldWithDefault(this, 1, 0));
};


/** @param {number} value */
proto.vmr.TransferHistoryResponse.prototype.setTo = function(value) {
  jspb.Message.setProto3IntField(this, 1, value);
};


/**
 * optional int64 timestamp = 2;
 * @return {number}
 */
proto.vmr.TransferHistoryResponse.prototype.getTimestamp = function() {
  return /** @type {number} */ (jspb.Message.getFieldWithDefault(this, 2, 0));
};


/** @param {number} value */
proto.vmr.TransferHistoryResponse.prototype.setTimestamp = function(value) {
  jspb.Message.setProto3IntField(this, 2, value);
};


/**
 * optional int64 amount = 3;
 * @return {number}
 */
proto.vmr.TransferHistoryResponse.prototype.getAmount = function() {
  return /** @type {number} */ (jspb.Message.getFieldWithDefault(this, 3, 0));
};


/** @param {number} value */
proto.vmr.TransferHistoryResponse.prototype.setAmount = function(value) {
  jspb.Message.setProto3IntField(this, 3, value);
};


/**
 * optional TransferStatus transfer_status = 4;
 * @return {!proto.vmr.TransferHistoryResponse.TransferStatus}
 */
proto.vmr.TransferHistoryResponse.prototype.getTransferStatus = function() {
  return /** @type {!proto.vmr.TransferHistoryResponse.TransferStatus} */ (jspb.Message.getFieldWithDefault(this, 4, 0));
};


/** @param {!proto.vmr.TransferHistoryResponse.TransferStatus} value */
proto.vmr.TransferHistoryResponse.prototype.setTransferStatus = function(value) {
  jspb.Message.setProto3EnumField(this, 4, value);
};



/**
 * Generated by JsPbCodeGenerator.
 * @param {Array=} opt_data Optional initial data array, typically from a
 * server response, or constructed directly in Javascript. The array is used
 * in place and becomes part of the constructed object. It is not cloned.
 * If no data is provided, the constructed object will be empty, but still
 * valid.
 * @extends {jspb.Message}
 * @constructor
 */
proto.vmr.ReceiveHistoryResponse = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, null, null);
};
goog.inherits(proto.vmr.ReceiveHistoryResponse, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.vmr.ReceiveHistoryResponse.displayName = 'proto.vmr.ReceiveHistoryResponse';
}


if (jspb.Message.GENERATE_TO_OBJECT) {
/**
 * Creates an object representation of this proto suitable for use in Soy templates.
 * Field names that are reserved in JavaScript and will be renamed to pb_name.
 * To access a reserved field use, foo.pb_<name>, eg, foo.pb_default.
 * For the list of reserved names please see:
 *     com.google.apps.jspb.JsClassTemplate.JS_RESERVED_WORDS.
 * @param {boolean=} opt_includeInstance Whether to include the JSPB instance
 *     for transitional soy proto support: http://goto/soy-param-migration
 * @return {!Object}
 */
proto.vmr.ReceiveHistoryResponse.prototype.toObject = function(opt_includeInstance) {
  return proto.vmr.ReceiveHistoryResponse.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.vmr.ReceiveHistoryResponse} msg The msg instance to transform.
 * @return {!Object}
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.vmr.ReceiveHistoryResponse.toObject = function(includeInstance, msg) {
  var f, obj = {
    from: jspb.Message.getFieldWithDefault(msg, 1, 0),
    timestamp: jspb.Message.getFieldWithDefault(msg, 2, 0),
    amount: jspb.Message.getFieldWithDefault(msg, 3, 0)
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg;
  }
  return obj;
};
}


/**
 * Deserializes binary data (in protobuf wire format).
 * @param {jspb.ByteSource} bytes The bytes to deserialize.
 * @return {!proto.vmr.ReceiveHistoryResponse}
 */
proto.vmr.ReceiveHistoryResponse.deserializeBinary = function(bytes) {
  var reader = new jspb.BinaryReader(bytes);
  var msg = new proto.vmr.ReceiveHistoryResponse;
  return proto.vmr.ReceiveHistoryResponse.deserializeBinaryFromReader(msg, reader);
};


/**
 * Deserializes binary data (in protobuf wire format) from the
 * given reader into the given message object.
 * @param {!proto.vmr.ReceiveHistoryResponse} msg The message object to deserialize into.
 * @param {!jspb.BinaryReader} reader The BinaryReader to use.
 * @return {!proto.vmr.ReceiveHistoryResponse}
 */
proto.vmr.ReceiveHistoryResponse.deserializeBinaryFromReader = function(msg, reader) {
  while (reader.nextField()) {
    if (reader.isEndGroup()) {
      break;
    }
    var field = reader.getFieldNumber();
    switch (field) {
    case 1:
      var value = /** @type {number} */ (reader.readInt64());
      msg.setFrom(value);
      break;
    case 2:
      var value = /** @type {number} */ (reader.readInt64());
      msg.setTimestamp(value);
      break;
    case 3:
      var value = /** @type {number} */ (reader.readInt64());
      msg.setAmount(value);
      break;
    default:
      reader.skipField();
      break;
    }
  }
  return msg;
};


/**
 * Serializes the message to binary data (in protobuf wire format).
 * @return {!Uint8Array}
 */
proto.vmr.ReceiveHistoryResponse.prototype.serializeBinary = function() {
  var writer = new jspb.BinaryWriter();
  proto.vmr.ReceiveHistoryResponse.serializeBinaryToWriter(this, writer);
  return writer.getResultBuffer();
};


/**
 * Serializes the given message to binary data (in protobuf wire
 * format), writing to the given BinaryWriter.
 * @param {!proto.vmr.ReceiveHistoryResponse} message
 * @param {!jspb.BinaryWriter} writer
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.vmr.ReceiveHistoryResponse.serializeBinaryToWriter = function(message, writer) {
  var f = undefined;
  f = message.getFrom();
  if (f !== 0) {
    writer.writeInt64(
      1,
      f
    );
  }
  f = message.getTimestamp();
  if (f !== 0) {
    writer.writeInt64(
      2,
      f
    );
  }
  f = message.getAmount();
  if (f !== 0) {
    writer.writeInt64(
      3,
      f
    );
  }
};


/**
 * optional int64 from = 1;
 * @return {number}
 */
proto.vmr.ReceiveHistoryResponse.prototype.getFrom = function() {
  return /** @type {number} */ (jspb.Message.getFieldWithDefault(this, 1, 0));
};


/** @param {number} value */
proto.vmr.ReceiveHistoryResponse.prototype.setFrom = function(value) {
  jspb.Message.setProto3IntField(this, 1, value);
};


/**
 * optional int64 timestamp = 2;
 * @return {number}
 */
proto.vmr.ReceiveHistoryResponse.prototype.getTimestamp = function() {
  return /** @type {number} */ (jspb.Message.getFieldWithDefault(this, 2, 0));
};


/** @param {number} value */
proto.vmr.ReceiveHistoryResponse.prototype.setTimestamp = function(value) {
  jspb.Message.setProto3IntField(this, 2, value);
};


/**
 * optional int64 amount = 3;
 * @return {number}
 */
proto.vmr.ReceiveHistoryResponse.prototype.getAmount = function() {
  return /** @type {number} */ (jspb.Message.getFieldWithDefault(this, 3, 0));
};


/** @param {number} value */
proto.vmr.ReceiveHistoryResponse.prototype.setAmount = function(value) {
  jspb.Message.setProto3IntField(this, 3, value);
};



/**
 * Generated by JsPbCodeGenerator.
 * @param {Array=} opt_data Optional initial data array, typically from a
 * server response, or constructed directly in Javascript. The array is used
 * in place and becomes part of the constructed object. It is not cloned.
 * If no data is provided, the constructed object will be empty, but still
 * valid.
 * @extends {jspb.Message}
 * @constructor
 */
proto.vmr.HistoryResponse = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, null, null);
};
goog.inherits(proto.vmr.HistoryResponse, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.vmr.HistoryResponse.displayName = 'proto.vmr.HistoryResponse';
}


if (jspb.Message.GENERATE_TO_OBJECT) {
/**
 * Creates an object representation of this proto suitable for use in Soy templates.
 * Field names that are reserved in JavaScript and will be renamed to pb_name.
 * To access a reserved field use, foo.pb_<name>, eg, foo.pb_default.
 * For the list of reserved names please see:
 *     com.google.apps.jspb.JsClassTemplate.JS_RESERVED_WORDS.
 * @param {boolean=} opt_includeInstance Whether to include the JSPB instance
 *     for transitional soy proto support: http://goto/soy-param-migration
 * @return {!Object}
 */
proto.vmr.HistoryResponse.prototype.toObject = function(opt_includeInstance) {
  return proto.vmr.HistoryResponse.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.vmr.HistoryResponse} msg The msg instance to transform.
 * @return {!Object}
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.vmr.HistoryResponse.toObject = function(includeInstance, msg) {
  var f, obj = {
    type: jspb.Message.getFieldWithDefault(msg, 1, 0),
    transferResponse: (f = msg.getTransferResponse()) && proto.vmr.TransferHistoryResponse.toObject(includeInstance, f),
    receiveResponse: (f = msg.getReceiveResponse()) && proto.vmr.ReceiveHistoryResponse.toObject(includeInstance, f)
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg;
  }
  return obj;
};
}


/**
 * Deserializes binary data (in protobuf wire format).
 * @param {jspb.ByteSource} bytes The bytes to deserialize.
 * @return {!proto.vmr.HistoryResponse}
 */
proto.vmr.HistoryResponse.deserializeBinary = function(bytes) {
  var reader = new jspb.BinaryReader(bytes);
  var msg = new proto.vmr.HistoryResponse;
  return proto.vmr.HistoryResponse.deserializeBinaryFromReader(msg, reader);
};


/**
 * Deserializes binary data (in protobuf wire format) from the
 * given reader into the given message object.
 * @param {!proto.vmr.HistoryResponse} msg The message object to deserialize into.
 * @param {!jspb.BinaryReader} reader The BinaryReader to use.
 * @return {!proto.vmr.HistoryResponse}
 */
proto.vmr.HistoryResponse.deserializeBinaryFromReader = function(msg, reader) {
  while (reader.nextField()) {
    if (reader.isEndGroup()) {
      break;
    }
    var field = reader.getFieldNumber();
    switch (field) {
    case 1:
      var value = /** @type {!proto.vmr.HistoryResponseType} */ (reader.readEnum());
      msg.setType(value);
      break;
    case 2:
      var value = new proto.vmr.TransferHistoryResponse;
      reader.readMessage(value,proto.vmr.TransferHistoryResponse.deserializeBinaryFromReader);
      msg.setTransferResponse(value);
      break;
    case 3:
      var value = new proto.vmr.ReceiveHistoryResponse;
      reader.readMessage(value,proto.vmr.ReceiveHistoryResponse.deserializeBinaryFromReader);
      msg.setReceiveResponse(value);
      break;
    default:
      reader.skipField();
      break;
    }
  }
  return msg;
};


/**
 * Serializes the message to binary data (in protobuf wire format).
 * @return {!Uint8Array}
 */
proto.vmr.HistoryResponse.prototype.serializeBinary = function() {
  var writer = new jspb.BinaryWriter();
  proto.vmr.HistoryResponse.serializeBinaryToWriter(this, writer);
  return writer.getResultBuffer();
};


/**
 * Serializes the given message to binary data (in protobuf wire
 * format), writing to the given BinaryWriter.
 * @param {!proto.vmr.HistoryResponse} message
 * @param {!jspb.BinaryWriter} writer
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.vmr.HistoryResponse.serializeBinaryToWriter = function(message, writer) {
  var f = undefined;
  f = message.getType();
  if (f !== 0.0) {
    writer.writeEnum(
      1,
      f
    );
  }
  f = message.getTransferResponse();
  if (f != null) {
    writer.writeMessage(
      2,
      f,
      proto.vmr.TransferHistoryResponse.serializeBinaryToWriter
    );
  }
  f = message.getReceiveResponse();
  if (f != null) {
    writer.writeMessage(
      3,
      f,
      proto.vmr.ReceiveHistoryResponse.serializeBinaryToWriter
    );
  }
};


/**
 * optional HistoryResponseType type = 1;
 * @return {!proto.vmr.HistoryResponseType}
 */
proto.vmr.HistoryResponse.prototype.getType = function() {
  return /** @type {!proto.vmr.HistoryResponseType} */ (jspb.Message.getFieldWithDefault(this, 1, 0));
};


/** @param {!proto.vmr.HistoryResponseType} value */
proto.vmr.HistoryResponse.prototype.setType = function(value) {
  jspb.Message.setProto3EnumField(this, 1, value);
};


/**
 * optional TransferHistoryResponse transfer_response = 2;
 * @return {?proto.vmr.TransferHistoryResponse}
 */
proto.vmr.HistoryResponse.prototype.getTransferResponse = function() {
  return /** @type{?proto.vmr.TransferHistoryResponse} */ (
    jspb.Message.getWrapperField(this, proto.vmr.TransferHistoryResponse, 2));
};


/** @param {?proto.vmr.TransferHistoryResponse|undefined} value */
proto.vmr.HistoryResponse.prototype.setTransferResponse = function(value) {
  jspb.Message.setWrapperField(this, 2, value);
};


proto.vmr.HistoryResponse.prototype.clearTransferResponse = function() {
  this.setTransferResponse(undefined);
};


/**
 * Returns whether this field is set.
 * @return {!boolean}
 */
proto.vmr.HistoryResponse.prototype.hasTransferResponse = function() {
  return jspb.Message.getField(this, 2) != null;
};


/**
 * optional ReceiveHistoryResponse receive_response = 3;
 * @return {?proto.vmr.ReceiveHistoryResponse}
 */
proto.vmr.HistoryResponse.prototype.getReceiveResponse = function() {
  return /** @type{?proto.vmr.ReceiveHistoryResponse} */ (
    jspb.Message.getWrapperField(this, proto.vmr.ReceiveHistoryResponse, 3));
};


/** @param {?proto.vmr.ReceiveHistoryResponse|undefined} value */
proto.vmr.HistoryResponse.prototype.setReceiveResponse = function(value) {
  jspb.Message.setWrapperField(this, 3, value);
};


proto.vmr.HistoryResponse.prototype.clearReceiveResponse = function() {
  this.setReceiveResponse(undefined);
};


/**
 * Returns whether this field is set.
 * @return {!boolean}
 */
proto.vmr.HistoryResponse.prototype.hasReceiveResponse = function() {
  return jspb.Message.getField(this, 3) != null;
};



/**
 * Generated by JsPbCodeGenerator.
 * @param {Array=} opt_data Optional initial data array, typically from a
 * server response, or constructed directly in Javascript. The array is used
 * in place and becomes part of the constructed object. It is not cloned.
 * If no data is provided, the constructed object will be empty, but still
 * valid.
 * @extends {jspb.Message}
 * @constructor
 */
proto.vmr.HistoryListResponse = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, proto.vmr.HistoryListResponse.repeatedFields_, null);
};
goog.inherits(proto.vmr.HistoryListResponse, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.vmr.HistoryListResponse.displayName = 'proto.vmr.HistoryListResponse';
}
/**
 * List of repeated fields within this message type.
 * @private {!Array<number>}
 * @const
 */
proto.vmr.HistoryListResponse.repeatedFields_ = [2];



if (jspb.Message.GENERATE_TO_OBJECT) {
/**
 * Creates an object representation of this proto suitable for use in Soy templates.
 * Field names that are reserved in JavaScript and will be renamed to pb_name.
 * To access a reserved field use, foo.pb_<name>, eg, foo.pb_default.
 * For the list of reserved names please see:
 *     com.google.apps.jspb.JsClassTemplate.JS_RESERVED_WORDS.
 * @param {boolean=} opt_includeInstance Whether to include the JSPB instance
 *     for transitional soy proto support: http://goto/soy-param-migration
 * @return {!Object}
 */
proto.vmr.HistoryListResponse.prototype.toObject = function(opt_includeInstance) {
  return proto.vmr.HistoryListResponse.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.vmr.HistoryListResponse} msg The msg instance to transform.
 * @return {!Object}
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.vmr.HistoryListResponse.toObject = function(includeInstance, msg) {
  var f, obj = {
    error: (f = msg.getError()) && vmr_error_pb.Error.toObject(includeInstance, f),
    historyResponseList: jspb.Message.toObjectList(msg.getHistoryResponseList(),
    proto.vmr.HistoryResponse.toObject, includeInstance)
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg;
  }
  return obj;
};
}


/**
 * Deserializes binary data (in protobuf wire format).
 * @param {jspb.ByteSource} bytes The bytes to deserialize.
 * @return {!proto.vmr.HistoryListResponse}
 */
proto.vmr.HistoryListResponse.deserializeBinary = function(bytes) {
  var reader = new jspb.BinaryReader(bytes);
  var msg = new proto.vmr.HistoryListResponse;
  return proto.vmr.HistoryListResponse.deserializeBinaryFromReader(msg, reader);
};


/**
 * Deserializes binary data (in protobuf wire format) from the
 * given reader into the given message object.
 * @param {!proto.vmr.HistoryListResponse} msg The message object to deserialize into.
 * @param {!jspb.BinaryReader} reader The BinaryReader to use.
 * @return {!proto.vmr.HistoryListResponse}
 */
proto.vmr.HistoryListResponse.deserializeBinaryFromReader = function(msg, reader) {
  while (reader.nextField()) {
    if (reader.isEndGroup()) {
      break;
    }
    var field = reader.getFieldNumber();
    switch (field) {
    case 1:
      var value = new vmr_error_pb.Error;
      reader.readMessage(value,vmr_error_pb.Error.deserializeBinaryFromReader);
      msg.setError(value);
      break;
    case 2:
      var value = new proto.vmr.HistoryResponse;
      reader.readMessage(value,proto.vmr.HistoryResponse.deserializeBinaryFromReader);
      msg.addHistoryResponse(value);
      break;
    default:
      reader.skipField();
      break;
    }
  }
  return msg;
};


/**
 * Serializes the message to binary data (in protobuf wire format).
 * @return {!Uint8Array}
 */
proto.vmr.HistoryListResponse.prototype.serializeBinary = function() {
  var writer = new jspb.BinaryWriter();
  proto.vmr.HistoryListResponse.serializeBinaryToWriter(this, writer);
  return writer.getResultBuffer();
};


/**
 * Serializes the given message to binary data (in protobuf wire
 * format), writing to the given BinaryWriter.
 * @param {!proto.vmr.HistoryListResponse} message
 * @param {!jspb.BinaryWriter} writer
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.vmr.HistoryListResponse.serializeBinaryToWriter = function(message, writer) {
  var f = undefined;
  f = message.getError();
  if (f != null) {
    writer.writeMessage(
      1,
      f,
      vmr_error_pb.Error.serializeBinaryToWriter
    );
  }
  f = message.getHistoryResponseList();
  if (f.length > 0) {
    writer.writeRepeatedMessage(
      2,
      f,
      proto.vmr.HistoryResponse.serializeBinaryToWriter
    );
  }
};


/**
 * optional Error error = 1;
 * @return {?proto.vmr.Error}
 */
proto.vmr.HistoryListResponse.prototype.getError = function() {
  return /** @type{?proto.vmr.Error} */ (
    jspb.Message.getWrapperField(this, vmr_error_pb.Error, 1));
};


/** @param {?proto.vmr.Error|undefined} value */
proto.vmr.HistoryListResponse.prototype.setError = function(value) {
  jspb.Message.setWrapperField(this, 1, value);
};


proto.vmr.HistoryListResponse.prototype.clearError = function() {
  this.setError(undefined);
};


/**
 * Returns whether this field is set.
 * @return {!boolean}
 */
proto.vmr.HistoryListResponse.prototype.hasError = function() {
  return jspb.Message.getField(this, 1) != null;
};


/**
 * repeated HistoryResponse history_response = 2;
 * @return {!Array<!proto.vmr.HistoryResponse>}
 */
proto.vmr.HistoryListResponse.prototype.getHistoryResponseList = function() {
  return /** @type{!Array<!proto.vmr.HistoryResponse>} */ (
    jspb.Message.getRepeatedWrapperField(this, proto.vmr.HistoryResponse, 2));
};


/** @param {!Array<!proto.vmr.HistoryResponse>} value */
proto.vmr.HistoryListResponse.prototype.setHistoryResponseList = function(value) {
  jspb.Message.setRepeatedWrapperField(this, 2, value);
};


/**
 * @param {!proto.vmr.HistoryResponse=} opt_value
 * @param {number=} opt_index
 * @return {!proto.vmr.HistoryResponse}
 */
proto.vmr.HistoryListResponse.prototype.addHistoryResponse = function(opt_value, opt_index) {
  return jspb.Message.addToRepeatedWrapperField(this, 2, opt_value, proto.vmr.HistoryResponse, opt_index);
};


proto.vmr.HistoryListResponse.prototype.clearHistoryResponseList = function() {
  this.setHistoryResponseList([]);
};


/**
 * @enum {number}
 */
proto.vmr.HistoryResponseType = {
  TRANSFER: 0,
  RECEIVE: 1
};

goog.object.extend(exports, proto.vmr);