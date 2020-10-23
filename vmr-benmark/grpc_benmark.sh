# !/bin/bash

JWT_TOKEN='eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTYwMjgxMzc2OH0.4Z__HsM9_Pqcwo29J9OPDJbNzO9Itt7i5QSvdj_0cro'

ghz -i ./../vmr-newbackend/src/main/proto --proto=vmr/wallet.proto \
  --call=vmr.WalletService.Transfer \
  -n 500 \
  -t 0 \
  -c 20 \
  -d '{"request_id":"{{.RequestNumber}}", "receiver":"2", "amount":1000,"message":"Foo", "password": "12345678"}' localhost:8082 \
  -m '{"x-jwt-token":"'"${JWT_TOKEN}"'"}' \
  --insecure

ghz -i ./../vmr-newbackend/src/main/proto --proto=vmr/wallet.proto \
  --call=vmr.WalletService.GetBalance \
  -n 500 \
  -c 20 \
  -d '{}' localhost:8082 \
  -m '{"x-jwt-token":"'"${JWT_TOKEN}"'"}' \
  --insecure

ghz -i ./../vmr-newbackend/src/main/proto --proto=vmr/wallet.proto \
  --call=vmr.WalletService.GetHistoryWithOffset \
  -n 500 \
  -d '{"offset":"{{.RequestNumber}}"}' localhost:8082 \
  -m '{"x-jwt-token":"'"${JWT_TOKEN}"'"}' \
  --insecure

ghz -i ./../vmr-newbackend/src/main/proto --proto=vmr/friend.proto \
  --call=vmr.FriendService.GetChatFriendList \
  -n 500 \
  -d '{}' localhost:8082 \
  -m '{"x-jwt-token":"'"${JWT_TOKEN}"'"}' \
  --insecure

ghz -i ./../vmr-newbackend/src/main/proto --proto=vmr/friend.proto \
  --call=vmr.FriendService.GetFriendList \
  -n 100000 \
  -d '{}' localhost:8082 \
  -m '{"x-jwt-token":"'"${JWT_TOKEN}"'"}' \
  --insecure

