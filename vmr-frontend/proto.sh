#!/bin/bash

OUT_DIR="./src"

protoc ./proto/*.proto \
  --js_out=import_style=commonjs,binary:$OUT_DIR \
  --grpc-web_out=import_style=commonjs,mode=grpcwebtext:$OUT_DIR

for filename in ./src/proto/*.js; do
  echo "$filename";
  content=$(cat "$filename")
  echo "/* eslint-disable */ $content" > "$filename"
done
