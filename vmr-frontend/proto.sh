#!/bin/bash

OUT_DIR="./src"

protoc ./proto/*.proto \
    --js_out=import_style=commonjs:$OUT_DIR \
    --grpc-web_out=import_style=commonjs,mode=grpcwebtext:$OUT_DIR
