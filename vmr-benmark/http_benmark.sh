# !/bin/bash

JWT_TOKEN='eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTYwMjgxMzc2OH0.4Z__HsM9_Pqcwo29J9OPDJbNzO9Itt7i5QSvdj_0cro'

wrk -t50 -c50 -d10s \
  --latency \
  -H "Authorization: Bearer $JWT_TOKEN" \
  http://localhost:8080/api/protected/chat/2/1


wrk -t50 -c50 -d10s \
  --latency \
  -s ./login.lua \
  http://localhost:8080/api/public/login

  
wrk -t1 -c50 -d10s \
  --latency \
  -s ./register.lua \
  http://localhost:8080/api/public/register
