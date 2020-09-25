#!/bin/bash

OUT_DIR="./src/proto"
IN_DIR="../vmr-newbackend/src/main/proto/vmr"

rm $OUT_DIR/*

cp -a "./$IN_DIR" "./proto"

# shellcheck disable=SC2046
protoc --proto_path=proto \
  --js_out=import_style=commonjs,binary:$OUT_DIR \
  --grpc-web_out=import_style=commonjs,mode=grpcwebtext:$OUT_DIR \
  $(find ./proto -type f -iname "*.proto")

for filename in ./src/proto/vmr/*.js; do
  echo "$filename"
  content=$(cat "$filename")
  echo "/* eslint-disable */ $content" >"$filename"
done
