# !/bin/bash

JWT_TOKEN='eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTYwMjgxMzc2OH0.4Z__HsM9_Pqcwo29J9OPDJbNzO9Itt7i5QSvdj_0cro'

ghz -i ./../vmr-newbackend/src/main/proto --proto=vmr/wallet.proto \
  --call=vmr.WalletService.Transfer \
  -n 2000 \
  -d '{"request_id":"{{.RequestNumber}}", "receiver":"2", "amount":1000,"message":"Foo", "password": "12345678"}' localhost:8082 \
  -m '{"x-jwt-token":"'"${JWT_TOKEN}"'"}' \
  --insecure

ghz -i ./../vmr-newbackend/src/main/proto --proto=vmr/wallet.proto \
  --call=vmr.WalletService.GetBalance \
  -n 2000 \
  -d '{}' localhost:8082 \
  -m '{"x-jwt-token":"'"${JWT_TOKEN}"'"}' \
  --insecure

ghz -i ./../vmr-newbackend/src/main/proto --proto=vmr/wallet.proto \
  --call=vmr.WalletService.GetHistoryWithOffset \
  -n 2000 \
  -d '{"offset":0}' localhost:8082 \
  -m '{"x-jwt-token":"'"${JWT_TOKEN}"'"}' \
  --insecure

ghz -i ./../vmr-newbackend/src/main/proto --proto=vmr/friend.proto \
  --call=vmr.FriendService.GetChatFriendList \
  -n 2000 \
  -d '{}' localhost:8082 \
  -m '{"x-jwt-token":"'"${JWT_TOKEN}"'"}' \
  --insecure

ghz -i ./../vmr-newbackend/src/main/proto --proto=vmr/friend.proto \
  --call=vmr.FriendService.GetFriendList \
  -n 2000 \
  -d '{}' localhost:8082 \
  -m '{"x-jwt-token":"'"${JWT_TOKEN}"'"}' \
  --insecure

